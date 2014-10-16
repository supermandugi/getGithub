package univ.incheon.irsl.obd_asd;

import android.app.Activity;
import android.content.Context;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

public class MainActivity extends Activity {

	private EditText edtTextAddress;
	private EditText edtTextPort;
	private Button btnConnect;
	private Button btnClear;
	private Button btnASD;
	private TextView txtResponse;
	
	Socket socket;
	String response="0";
	DataOutputStream dos;
	DataInputStream din;
	String txtReceive ="";
	Scanner scanner;

	int size;
	byte[] w = new byte[1024];

	NetworkTask myClientTask;
	
	AudioManager am;
	Record record;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		edtTextAddress = (EditText) findViewById(R.id.address);
		edtTextPort = (EditText) findViewById(R.id.port);
		btnConnect = (Button) findViewById(R.id.connect);
		btnClear = (Button) findViewById(R.id.clear);
		
		btnASD = (Button)findViewById(R.id.asd_on);
		
		txtResponse = (TextView) findViewById(R.id.response);
		
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		btnASD.setOnClickListener(new OnClickListener()
        {
        	public void onClick(View v)
        	{
        		record = new Record();
        		record.start();
        	}
        });
		
		btnConnect.setOnClickListener(buttonConnectOnClickListener);
		btnClear.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				txtResponse.setText("Close");
			}
		});
		
		for (int a=0; a<buffersize-1; a++)
    	{
    		samples[a]=0;
    	}
       genTone(); 
       
	}
	
	OnClickListener buttonConnectOnClickListener = new OnClickListener(){
		public void onClick(View arg0){
			NetworkTask myClientTask = new NetworkTask(
					edtTextAddress.getText().toString(),
					Integer.parseInt(edtTextPort.getText().toString())
					);
			myClientTask.execute();
		}
	};
	
	public class NetworkTask extends AsyncTask<String, Integer, String>{
		
		String dstAddress;
		int dstPort;

		NetworkTask(String addr, int port){
			dstAddress = addr;
			dstPort = port;
		}
		
		@Override 
		 protected void onPreExecute() {
		       super.onPreExecute(); 
		 }
		
		@Override
		protected String doInBackground(String... arg0){
			
			try{
				Log.e("socket","Init");
				Socket socket = new Socket(dstAddress, dstPort);

				Log.e("socket","pass");
				
				dos = new DataOutputStream(socket.getOutputStream());

			    socket.setTcpNoDelay(true);

			    dos.writeUTF("010c"+"\r");

	    		Log.e("Send","010C");

		    	while(socket != null && socket.isConnected())
			    {

			    	//dos.writeUTF("010C"+"\r");
			    	//Log.e("send","010C");
			    	//dos.writeBytes("\r");

			    	//byte tmp1 = din.readByte();
			    	
			    	
			    	
			    	//txtReceive = new String(w,0,tmp1,"UTF-8");

			    	//Log.e("test",tmp1);
			    	
			    	size = socket.getInputStream().read(w);
			    	
			    	if (size <=0) continue;
			    	txtReceive = new String(w,0,size,"UTF-8");
			    		    	
			    	Log.e("rcv",txtReceive);
			    	
			    	int index = txtReceive.indexOf("0C");
			    	//Log.e("index",Integer.toString(index));
			    	if(index!=-1)
			    	{
			    		int start = index+3;
			    		int end = start+4;
			    		String tmp = txtReceive.substring(start,end+1);
			    		//Log.e("tmp",tmp);
			    		String[] str = new String(tmp).split(" ");
			    	
				    	int i = ((Integer.parseInt(str[0],16) * 256 + Integer.parseInt(str[1],16))/4);
				    	response = Integer.toString(i);
				    	freqc1 = i/60;
				    	//Log.e("receive",rcv);
				    	Log.e("result",response);
				    }
			    	//String[] str = new String(txtReceive).split("\r");
			    	//response = str[1];
			    	
			    	//response=txtReceive;
			    	
			    	try
					{
						Thread.sleep(10);
						
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
			    	
				    publishProgress(1);

			    }
			    
				//
				
			}catch(UnknownHostException e){
				try {
					socket.close();
					txtResponse.setText("Close");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}catch(IOException e){
				try {
					socket.close();
					txtResponse.setText("Close");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer...integers){
			//Log.e("Prog","pass");
			txtResponse.setText(response);
			
		}
		
		@Override
		protected void onPostExecute(String result){
			Log.e("Post","End");
			super.onPostExecute(result);
		}
	}
	public int duration; // seconds
	   public int sampleRate =8000;
	   int buffersize= 1;
	   public float sample,samples[] = new float[buffersize];
	   public int i=1,j;
	   public float freqc1=0, freqc12;
	   public float freqOfTone1=0,freqOfTone2=0,freqOfTone3,freqOfTone4,freqOfTone5,freqOfTone6,freqOfTone7,freqOfTone8=0,freqOfTone9=0;
	   public float tone1=0,tone2=0,tone3=0,tone4=0,tone5=0,tone6=0,tone7=0,tone8=0,tone9=0;
	   public float time;  
	   public byte generatedSnd[]= new byte[2 * buffersize], generatedSnd2[];
	   boolean isRecording; //currently not used


	public void genTone()
	   {
	      float out1=0,out2=0,out3=0,out4=0,out5=0,out6=0,out7=0,out8=0,out9=0;        
	      float tone0=0, tone=0;
	      
	      for (int m=0; m<buffersize; m++)
	      {
	         time= (float) i/(float)sampleRate;


	         tone0= (freqc12-freqc1)*time+tone1;
	         tone = freqc1*time+tone0;
	         tone1 = tone0;

	         out1 =  (float) Math.sin(2 * Math.PI * tone);

	         tone0 = 2*(freqc12-freqc1)*time+tone2;
	         tone = freqc1*2*time+tone0;
	         tone2 = tone0;

	         out2 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 3*(freqc12-freqc1)*time+tone3;
	         tone = freqc1*3*time+tone0;
	         tone3 = tone0;

	         out3 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 4*(freqc12-freqc1)*time+tone4;
	         tone = freqc1*4*time+tone0;
	         tone4 = tone0;

	         out4 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 5*(freqc12-freqc1)*time+tone5;
	         tone = freqc1*5*time+tone0;
	         tone5 = tone0;

	         out5 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 6*(freqc12-freqc1)*time+tone6;
	         tone = freqc1*6*time+tone0;
	         tone6 = tone0;

	         out6 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 7*(freqc12-freqc1)*time+tone7;
	         tone = freqc1*7*time+tone0;
	         tone7 = tone0;

	         out7 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 8*(freqc12-freqc1)*time+tone8;
	         tone = freqc1*8*time+tone0;
	         tone8 = tone0;

	         out8 =  (float)Math.sin(2 * Math.PI * tone);

	         tone0 = 9*(freqc12-freqc1)*time+tone9;
	         tone = freqc1*9*time+tone0;
	         tone9 = tone0;

	         out9 =  (float)Math.sin(2 * Math.PI * tone);

	             samples[m] = (out1+out2+out3+out4+out5+out6+out7+out8+out9)/10;
	      
	         i++;
	         freqc12=freqc1;
	      }
	      
	      int idx = 0;
	      for (float dVal : samples)
	      {
	         short val = (short) (dVal * 32767);
	         generatedSnd[idx++] = (byte) (val & 0x00ff);
	         generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
	      }   
	   }
	
	public class Record extends Thread
	{

	    public void run() {
	    	
	    	
	    	
	    	try{

	        android.os.Process.setThreadPriority
	        (android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

	        AudioTrack atrack = new AudioTrack
	        (AudioManager.STREAM_MUSIC,
	
	                sampleRate,
	                AudioFormat.CHANNEL_CONFIGURATION_MONO,
	                AudioFormat.ENCODING_PCM_16BIT,
	                buffersize*2,
	                AudioTrack.MODE_STREAM);

	        am.setRouting(AudioManager.MODE_NORMAL,
	                AudioManager.ROUTE_EARPIECE,

	                AudioManager.ROUTE_ALL);

	        Log.d("SPEAKERPHONE", "Is speakerphone on? : " + am.isSpeakerphoneOn());

	        atrack.setPlaybackRate(sampleRate);

	        atrack.play();

	        Thread.currentThread();
	        	while(!Thread.interrupted())
	        	{
	        	//freqOfTone2 = Integer.parseInt(response)/60;
	        		freqOfTone2 = Integer.parseInt(txtResponse.getText().toString())/30;
	        	atrack.write(generatedSnd, 0, buffersize*2);
	            
	        	genTone();    
	        }
	        atrack.stop();
	        isRecording = false;
			}
		finally
		{
		
		}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
