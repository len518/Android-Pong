package com.android.gf3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
 
public class ClientePong extends Activity {
	  private int x = 350; //x de la pelota
      private int y = 0;   //y de la pelota
      public int direcx = 1;   //direccion de la bola
      public int direcy = 1;   //
      public int speed = 4; 	//velocidad de la pelota
      private int _x = 2;		//x de la barra (local)
      private int _y = 20;		//y de la barra (local)
      private int ye = 0;		//y de la barra enemiga
      private BufferedReader input;
      private PrintWriter out;
      private Socket s;
      private DatagramSocket socketUDP;
      private DatagramPacket sendPacket;
      private DatagramPacket receivePacket;
      private int bwidth;
      private int bheight;
      private int width;  //screen resol
      private int height; //screen resol
      
      private ThreadRed threadr;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		threadr = new ThreadRed();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        //--------------------------obtener la Screen Resol-------------------------------------
        Display display = getWindowManager().getDefaultDisplay(); 
        width = display.getWidth();
        height = display.getHeight();
        _y = height/2; 
        //--------------------------------------------------------------------------------------
        Log.d("gf3","w "+ width);
        Log.d("gf3","h "+ height);
        threadr.setRunning(true);
        threadr.start();
        setContentView(new Panel(this));
        
    }
 
    class Panel extends SurfaceView implements SurfaceHolder.Callback {
        private TutorialThread _thread;
        private int sheight; 
        private Bitmap _scratch;
      	private Bitmap ball;
      	private Paint paint;
      	public Panel(Context context) {
            super(context);
            getHolder().addCallback(this);
            
            _thread = new TutorialThread(getHolder(), this);
            _scratch = BitmapFactory.decodeResource(getResources(), R.drawable.bar2);
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            bwidth = ball.getWidth();
            bheight = ball.getHeight();
            sheight = _scratch.getHeight();
            paint = new Paint();
            paint.setTextSize(20);
            setFocusableInTouchMode(true);
        }
     
        
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event){
        	super.onKeyDown(keyCode, event);
        	switch(keyCode)
            {
            case KeyEvent.KEYCODE_MENU:
            	//if(con){
            	//out.println("end");
            	//}
            	
            	x = 170;
        		Log.d("gf3", "se apacho menu");
        		try {	
					s.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
            }
        	return false;
        }
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //_x = (int) event.getX();
            _y = (int) event.getY();
            return true;
        }
        @Override
        public void onDraw(Canvas canvas) {
            
            
            canvas.drawColor(Color.RED);
            canvas.drawBitmap(_scratch, _x , _y - (sheight / 2), null);
            canvas.drawBitmap(ball, x , y, null);
            canvas.drawBitmap(_scratch, 750 , ye, null);
            canvas.drawText("0|1", width/2, 20,paint);
            
        }
     
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }
     
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        	
        	_thread.setRunning(true);
            _thread.start();
            
           
        }
     
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // simply copied from sample application LunarLander:
            // we have to tell thread to shut down & wait for it to finish, or else
            // it might touch the Surface after we return and explode
            boolean retry = true;
            _thread.setRunning(false);
            threadr.setRunning(false);
            while (retry) {
                try {
                	threadr.join();
                    _thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    // we will try it again and again...
                }
            }
        }
    }
 
    class TutorialThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private Panel _panel;
        private boolean _run = false;
 
        public TutorialThread(SurfaceHolder surfaceHolder, Panel panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }
 
        public void setRunning(boolean run) {
            _run = run;
        }
 
        @Override
        public void run() {
            Canvas c;
            while (_run) {
                c = null;
                try {
                	//if(con){
                	//out.println("asdf");
                	//}
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {
                    	if(x >= width - bwidth ){
                    		direcx= -1;
                    	}if(y >= height - bheight){
                    		direcy= -1;
                    	}
                    	
                    	if((x <= 40)&&(y >=_y-95)&&(y <=_y+55)){
                    		direcx = 1;
                    		//direcy = 1;
                    	}else{
                    		
                    	}
                    	
                    	if(y < 2){
                    		direcy = 1;
                    	}
                    	
                    	
                    	x = x+direcx*speed;
                    	y = y+ direcy*speed;
                        _panel.onDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
            //if(con){
            //out.println("end");
            //}
            	
        		//y = 50;
        		//if(con){
				//s.close();
        		//}
			
        }
    }
    
   //-------------------------------------------------- RED --------------------------- 
    private boolean con = false;
    class ThreadRed extends Thread {
        private boolean running = false;
        //private SurfaceHolder surfaceHolder;
        //private Panel panel;
        public ThreadRed(SurfaceHolder sh, Panel p) {
        	//surfaceHolder = sh;
            //panel = p;
        }
        public ThreadRed(){
        	
        }
        
 
        public void setRunning(boolean run) {
            running = run;
        }
 
        @Override
        public void run() {
        	 byte[] res; 
        	 int id;
        	 int anterior = 0;
        	 byte cicle;
        	try {
                /* s = new Socket("192.168.1.104",8888);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
                out.println("hola");
                Log.d("Cliente", "empezando el cliente");
                input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                ye = Integer.parseInt(input.readLine());
                con = true; */
        		  //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        		  String messageStr="hello";
        	      socketUDP = new DatagramSocket();
        	      InetAddress IPAddress = InetAddress.getByName("192.168.1.104");
        	      byte[] sendData = messageStr.getBytes();
        	      byte[] receiveData = new byte[1024];
        	      sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8888);
        	      socketUDP.send(sendPacket);
        	      receivePacket = new DatagramPacket(receiveData,receiveData.length);
        	      socketUDP.receive(receivePacket);
        	      ye = receivePacket.getData()[0];
        	      
        	     

    	    } catch (UnknownHostException e) {
                running = false;
                e.printStackTrace();
    	    } catch (IOException e) {
    	    	running = false;
    	    	// TODO Auto-generated catch block
                e.printStackTrace();
    	    }
            while (running) {
                //c = null;
                try {
                	//-------------------------------------------------------------------------
                	
                	try {
                		
              	      	//socketUDP.send(sendPacket);
              	      	socketUDP.receive(receivePacket);
              	      	res = receivePacket.getData();
              	      	id = res[2];
              	      	Log.d("gf3",""+id);
              	      
              	      	if(id > anterior){
              	      		
              	      		anterior = id;
              	      		ye = res[0]*4 + res[1];
              	      		Log.d("gf3","--"+res[0]);
              	      	}
              	  	if(id > 124){
          	      		anterior = 0;
          	      	}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	//-------------------------------------------------------------------------
                	
                	//out.println(_y);
                	//String st = input.readLine();
                	//formato sera	x  de la bola
                	//				y  de la bola
                	//  			ye  el y del enemigo
                	//String res[] = st.split(";");
                	
                    //c = surfaceHolder.lockCanvas(null);
                    //synchronized (surfaceHolder) {
                    	
                     //   panel.onDraw(c);
                    //}
                
				} finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                   
                }
            }
            
            
        }
    }
}