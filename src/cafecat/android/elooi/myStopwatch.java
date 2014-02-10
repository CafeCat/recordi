package cafecat.android.elooi;

public class myStopwatch {
	private long startTime;
	private long stopTime;
	private long timeLost;
	
	private boolean running;
	private int num;
	
	public void start()
	{
		this.running = true;
		num=0;
		timeLost=0;
		this.startTime = System.currentTimeMillis();
	}
	public void stop()
	{
		this.stopTime = System.currentTimeMillis();
		this.running = false;
	}
	public void pause()
	{
		this.running = false;
		stopTime = System.currentTimeMillis();
	}
	public void resume()
	{
		this.running = true;
		this.timeLost = this.timeLost+System.currentTimeMillis()-stopTime;
	}
	public long getElapsedTime()
	{
		if(this.running)
		{
			if(timeLost==0)
			{
				return System.currentTimeMillis() - startTime;
			}else{
				return System.currentTimeMillis()-startTime-timeLost;
			}
		}else{
			if(timeLost==0)
			{
				return stopTime - startTime;
			}else{
				return stopTime-startTime-timeLost;
			}
		}
	}
	public double getAvergeSecs()
	{
		long elapsedTime = getElapsedTime();
		double result;
		result = (double)elapsedTime/1000;
		
		return result;
	}
	public int getAvergeCleanSecs()
	{
		int result=0;
		result = Math.round((float)getAvergeSecs());
		return result;
	}
	public String formatDigitSecs(int seconds)
	{
		String result="";
		if(seconds<10)
		{
			result = "0"+Integer.toString(seconds);
		}else{result = Integer.toString(seconds);}
		return result;
	}
}
