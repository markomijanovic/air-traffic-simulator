package gui;

public class AppTimer extends Thread {
	
	public interface TimerListener{
		void onWarning(int secondsRemaining);
		void onExpire();
		void onContinue();
	}

	private final int limitSeconds;
	private int counter;
	private boolean running;
	private boolean paused;
	private final TimerListener listener;
	
	public AppTimer(int limitSeconds,TimerListener timerListener) {
		this.listener = timerListener;
		this.limitSeconds=limitSeconds;
		this.running=false;
		this.paused=false;
		this.counter=0;
	}
	
	@Override
	public void run() {
		running=true;
		counter=0;
		
		while(running) {
			try {
				Thread.sleep(1000);
				
				if(paused) {
					Thread.sleep(1000);
					continue;
				} 
				
				counter++;
				int remaining=limitSeconds-counter;
				if(remaining<=5 && remaining>0&&listener !=null) {
					listener.onWarning(remaining);
				}
				
				if(counter>=limitSeconds) {
					if(listener!=null)listener.onExpire();
					running=false;
				}
			}catch (InterruptedException e) {
				running=false;
			}
		}
	}
	
	public void pauseTimer() {
		paused=true;
	}
	public synchronized void reset() {
		counter=0;
		paused=false;
	}
	
	public synchronized void stopTimer() {
		running=false;
	}
	public synchronized void ContinueWork() {
		paused=false;
		if(listener!=null) listener.onContinue();
		reset();
	}
}
