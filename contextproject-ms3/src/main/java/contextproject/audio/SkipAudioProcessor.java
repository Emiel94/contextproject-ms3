package contextproject.audio;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class SkipAudioProcessor implements AudioProcessor {
  
  private double secondsToSkip;
  private SkipAudioProcessorCallback callback;
  private boolean stopped = false;
  private boolean waitForNotify;

  public interface SkipAudioProcessorCallback {
    public void onFinished();
  }
  
  /**
   * Because of the way MP3 streaming works, we can't use dispatcher.skip().
   * Instead, this processor silently goes through the stream and cancels
   * the rest of the dispatcher chain until the desired point is reached.
   * 
   */
  public SkipAudioProcessor(double secondsToSkip, boolean waitForNotify, SkipAudioProcessorCallback callback) {
    // TODO: get starting point based on track beatgrid
    this.waitForNotify = waitForNotify;
    this.callback = callback;
    this.secondsToSkip = secondsToSkip;
  }
  
  @Override
  public boolean process(AudioEvent audioEvent) {
    if (stopped) {
      return true;
    }
    
    // If the desired point is reached,
    // stop the dispatcher thread from continuing any further.
    if (audioEvent.getTimeStamp() > secondsToSkip) {
      callback.onFinished();
      stopped = true;
      if(waitForNotify) {
        blockThread();
      }
    }
    
    return false;
  }
  
  public void blockThread() {
    try {
      synchronized (this) {
        wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void processingFinished() {
    
  }
}
