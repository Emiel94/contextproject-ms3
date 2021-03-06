package contextproject.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd;
import be.tarsos.dsp.WaveformSimilarityBasedOverlapAdd.Parameters;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.transcoder.Attributes;
import be.tarsos.transcoder.Streamer;
import be.tarsos.transcoder.ffmpeg.EncoderException;

import contextproject.audio.SkipAudioProcessor.SkipAudioProcessorCallback;
import contextproject.audio.transitions.BaseTransition;
import contextproject.controllers.PlayerControlsController;
import contextproject.helpers.StackTrace;
import contextproject.models.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;

public class TrackProcessor implements AudioProcessor {

  private static Logger log = LogManager.getLogger(TrackProcessor.class.getName());

  private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private PlayerControlsController pcc;
  // State
  private PlayerState state;
  private Track track;

  // Data
  private Attributes attributes;
  private AudioFormat format;

  // Audio Processing
  private AudioInputStream inputStream;
  private TarsosDSPAudioInputStream tarsosStream;
  private AudioPlayer audioPlayer;
  private WaveformSimilarityBasedOverlapAdd wsola;
  private GainProcessor gainProcessor;
  private CustomAudioDispatcher dispatcher;
  private SkipAudioProcessor skipProcessor;
  private ProgressProcessor progressProcessor;
  private Thread thread;

  private double tempo;
  private double currentTime;
  private double secondsToSkip;

  private double transitionTime;
  private BaseTransition transition;
  private boolean hasTransitioned;

  /**
   * This class acts as an audio player.
   */
  public TrackProcessor(Attributes attributes) {
    try {
      this.attributes = attributes;
      this.format = Streamer.streamAudioFormat(attributes);
    } catch (EncoderException e) {
      log.error("Error occured in TrackProcessor while calling constructor");
      log.trace(StackTrace.stackTrace(e));
    }
  }

  /**
   * Loads the Track that is specified.
   */
  public void load(Track track, double startGain, double startBpm) throws EncoderException,
      LineUnavailableException {
    if (state != PlayerState.NO_FILE_LOADED) {
      this.unload();
    }

    this.track = track;
    this.tempo = 1.0;
    this.currentTime = 0;
    this.transitionTime = getTransitonTime();
    this.secondsToSkip = getSecondsToSkip();
    this.setupDispatcherChain(startGain, startBpm, secondsToSkip);
    setState(PlayerState.FILE_LOADED);
  }

  private double getTransitonTime() {
    ArrayList<Double> ott = track.getOutTransitionTimes();
    if (ott != null && ott.size() > 1) {
      return ott.get(0);
    } else {
      return track.getDuration();
    }
  }

  private double getSecondsToSkip() {
    ArrayList<Double> itt = track.getInTransitionTimes();
    if (itt != null && !itt.isEmpty()) {
      return itt.get(0);
    } else {
      return 0;
    }
  }

  public double getTempo() {
    return tempo;
  }

  public void setTempo(double tempo) {
    this.tempo = tempo;
  }

  /**
   * Unloads the current track.
   */
  public void unload() {
    if (dispatcher != null) {
      dispatcher.stop();
      dispatcher = null;
    }
    track = null;
    setState(PlayerState.NO_FILE_LOADED);
  }

  /**
   * Play the current track.
   */
  public void play() {
    if (state != PlayerState.READY) {
      throw new IllegalStateException("Track processor is not ready yet.");
    }
    // Dispatchers are already running, but skipProcessor is blocking
    // the thread entirely. If we resume it, the track starts playing instantly!
    synchronized (skipProcessor) {
      skipProcessor.notify();
    }
    setState(PlayerState.PLAYING);
  }
  /**
   * pauses the player.
   * 
   * @throws LineUnavailableException
   *           line error.
   * @throws EncoderException
   *           encode error.
   */
  @SuppressWarnings("deprecation")
  public void pause() throws EncoderException, LineUnavailableException {
    if (this.state != PlayerState.PLAYING) {
      throw new IllegalStateException("Track processor is not even playing");
    }
    thread.stop();
    dispatcher.stop();
    this.setupDispatcherChain(1.0, 1.0, currentTime);
    setState(PlayerState.PAUSED);
  }

  public void setGain(double gain) {
    gainProcessor.setGain(gain);
  }

  /**
   * Initializes and starts the dispatcher chain so the player can play. This method is called when
   * a track is first loaded (this.load()).
   * 
   * @param startGain
   *          start gain
   * @param startBpm
   *          start BPM
   * @throws EncoderException
   *           encode error
   * @throws LineUnavailableException
   *           line error
   */
  private void setupDispatcherChain(double startGain, double startBpm, double secondsToSkip)
      throws EncoderException, LineUnavailableException {
    // Initialize the correct stream objects from file
    inputStream = Streamer.stream(track.getPath(), attributes);
    tarsosStream = new JVMAudioInputStream(inputStream);

    // Initialize audio processors
    audioPlayer = new AudioPlayer(format);
    wsola = new WaveformSimilarityBasedOverlapAdd(newParameters());
    gainProcessor = new GainProcessor(startGain);
    dispatcher = new CustomAudioDispatcher(tarsosStream, wsola.getInputBufferSize(),
        wsola.getOverlap());
    progressProcessor = new ProgressProcessor(track.getDuration(), this.secondsToSkip, pcc);

    // skipProcessor makes sure that the player skips until the desired point in time.
    // After that, we set our processor state to READY, so this.play can be called.
    final TrackProcessor thisProcessor = this;
    skipProcessor = new SkipAudioProcessor(secondsToSkip, true, new SkipAudioProcessorCallback() {
      @Override
      public void onFinished() {
        thisProcessor.setState(PlayerState.READY);
      }
    });

    // Setup the entire dispatcher chain
    dispatcher.addAudioProcessor(this);
    dispatcher.addAudioProcessor(skipProcessor);
    wsola.setDispatcher(dispatcher);
    dispatcher.addAudioProcessor(wsola);
    dispatcher.addAudioProcessor(gainProcessor);
    dispatcher.addAudioProcessor(progressProcessor);
    dispatcher.addAudioProcessor(audioPlayer);
    thread = new Thread(dispatcher);
    thread.start();
  }

  private Parameters newParameters() {
    return Parameters.musicDefaults(tempo, format.getSampleRate());
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    this.pcs.removePropertyChangeListener(listener);
  }

  public PlayerState getState() {
    return this.state;
  }

  private void setState(PlayerState state) {
    PlayerState oldState = state;
    this.state = state;
    this.pcs.firePropertyChange("state", oldState, state);
    log.info("TrackProcessor #" + this.hashCode() + " state changed: " + state.toString());
  }

  public static enum PlayerState {
    NO_FILE_LOADED, FILE_LOADED, READY, PLAYING, PAUSED, STOPPED
  }

  /**
   * Set up a transition.
   * 
   * @param transitionTime
   *          time to transit
   * @param transition
   *          the transition
   */
  public void setupTransition(double transitionTime, BaseTransition transition) {
    this.transitionTime = transitionTime;
    this.hasTransitioned = false;
    this.transition = transition;
  }

  @Override
  public boolean process(AudioEvent audioEvent) {
    currentTime = audioEvent.getTimeStamp();
    if (transitionTime == 0) {
      return true;
    }

    if (!hasTransitioned && currentTime > transitionTime) {
      new Thread(transition).start();
      hasTransitioned = true;
    }
    return true;
  }

  @Override
  public void processingFinished() {
  }

  public Track getTrack() {
    return track;
  }

  public void setPcc(PlayerControlsController pcc) {
    this.pcc = pcc;
  }
}
