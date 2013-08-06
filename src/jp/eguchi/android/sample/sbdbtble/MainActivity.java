package jp.eguchi.android.sample.sbdbtble;

import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements LeScanCallback
{
	private final String TAG ="SBDBT_BLE";
	
	private BluetoothManager bm = null;
	private BluetoothAdapter ba = null;
	private BluetoothDevice bd = null;
	private BluetoothGatt bg = null;
	
	private boolean bConnected = false;
	private byte send_data = 0x31;
	
	private Handler mHandler = null;

	private Button btn_con = null;
	private Button btn01 = null;
	private Button btn02 = null;
	private Button btn03 = null;
	private Button btn04 = null;
	private Button btn05 = null;
	private Button btn06 = null;
	private Button btn07 = null;
	
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
	{

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.d(TAG,"onCharacteristicChanged");
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if(status != BluetoothGatt.GATT_SUCCESS)
			{
				Log.d(TAG,"onCharacteristicRead Error=" + status);
				
				if(status == 133)
				{
					Log.d(TAG,"desable Bluetooth");
					ba.disable();
				}
			}
			else
			{
				if(characteristic.getUuid().equals(UUID.fromString("00003010-0000-1000-8000-00805f9b34fb")) == true)
				{
					int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0);
					
					Log.d(TAG,"0x10 Read=" + value);
					
					if(value != 0x31)
					{
						step5();
					}
					else
					{
						step6();
					}
				}
			}
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if(status != BluetoothGatt.GATT_SUCCESS)
			{
				Log.d(TAG,"onCharacteristicWrite: " + status);
				Log.d(TAG,"UUID: " + characteristic.getUuid().toString());
			}
			else
			{
				if(characteristic.getUuid().equals(UUID.fromString("00003010-0000-1000-8000-00805f9b34fb")) == true)
				{
					step4();
				}
			}
		}

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {

			
			switch(newState)
			{
				case android.bluetooth.BluetoothProfile.STATE_DISCONNECTED:
					Log.d(TAG,"STATE_DISCONNECTED");
					bConnected = false;
					mHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							btn_con.setText(R.string.connect);
							
							btn01.setEnabled(false);
							btn02.setEnabled(false);
							btn03.setEnabled(false);
							btn04.setEnabled(false);
							btn05.setEnabled(false);
							btn06.setEnabled(false);
							btn07.setEnabled(false);
						}
					});
					break;
					
				case android.bluetooth.BluetoothProfile.STATE_CONNECTING:
					Log.d(TAG,"STATE_CONNECTING");
					break;
					
				case android.bluetooth.BluetoothProfile.STATE_CONNECTED:
					Log.d(TAG,"STATE_CONNECTED");
					
					bConnected = true;
					
					mHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							btn_con.setText(R.string.disconnect);

							btn01.setEnabled(true);
							btn02.setEnabled(true);
							btn03.setEnabled(true);
							btn04.setEnabled(true);
							btn05.setEnabled(true);
							btn06.setEnabled(true);
							btn07.setEnabled(true);
						}
					});
					break;

				case android.bluetooth.BluetoothProfile.STATE_DISCONNECTING:
					Log.d(TAG,"STATE_DISCONNECTING");
					break;
			}
		}

		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status)
		{
			Log.d(TAG,"onDescriptorRead");
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status)
		{
			Log.d(TAG,"onDescriptorWrite");
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
		{
			Log.d(TAG,"onReadRemoteRssi");
		}

		@Override
		public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
		{
			Log.d(TAG,"onReliableWriteCompleted");
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status)
		{
			if (status != BluetoothGatt.GATT_SUCCESS)
			{
				Log.d(TAG,"onServicesDiscovered:" + status);
				return;
			}
			
			step4();
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn_con = (Button)findViewById(R.id.btn_connect);
		btn01 = (Button)findViewById(R.id.btn01);
		btn02 = (Button)findViewById(R.id.btn02);
		btn03 = (Button)findViewById(R.id.btn03);
		btn04 = (Button)findViewById(R.id.btn04);
		btn05 = (Button)findViewById(R.id.btn05);
		btn06 = (Button)findViewById(R.id.btn06);
		btn07 = (Button)findViewById(R.id.btn07);

		Button btn_con = (Button)findViewById(R.id.btn_connect);
		
		btn01.setEnabled(false);
		btn02.setEnabled(false);
		btn03.setEnabled(false);
		btn04.setEnabled(false);
		btn05.setEnabled(false);
		btn06.setEnabled(false);
		btn07.setEnabled(false);
		
		if(bConnected == true)
		{
			btn_con.setText(R.string.disconnect);
		}
		else
		{
			btn_con.setText(R.string.connect);
		}

		btn_con.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(bConnected == false)
				{
					step1();
				}
				else
				{
					bg.disconnect();
				}
				
			}
		});		
		
		btn01.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x31);
			}
		});
		
		btn02.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x32);
			}
		});
		
		btn03.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x33);
			}
		});

		btn04.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x34);
			}
		});

		btn05.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x35);
			}
		});

		btn06.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x36);
			}
		});

		btn07.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				step3((byte)0x37);
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mHandler = new Handler();
	}
	
	@Override
	protected void onPause()
	{
		if(ba != null)
		{
			ba.stopLeScan(this);
		}
		
		if(bg != null)
		{
			bg.disconnect();
			bg = null;
		}
		
		super.onPause();
	}

	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	{
		Log.d(TAG,"onLeScan");
		Log.d(TAG,"name=" + device.getName());
		Log.d(TAG,"addr=" + device.getAddress());
		
		if(device.getName().equals("SBBLE") == true)
		{
			ba.stopLeScan(this);
			step2(device.getAddress());
		}
	}

	void step1()
	{
		Log.d(TAG,"step1()");
		
		if(bm == null)
		{
			bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		}
		
		if(ba == null)
		{
			ba = bm.getAdapter();
		}
		
		if(ba.isEnabled() == false)
		{
			ba.enable();
		}
		else
		{
			if(ba != null)
			{
				boolean bflag = ba.startLeScan(this);
		
				if(bflag == false)
				{
					Log.d(TAG,"startLeScan is false");
					ba.stopLeScan(this);
				}
			}
		}
	}
	
	void step2(String addr)
	{
		Log.d(TAG,"step2");
		
		boolean bflag = false;
		
		if(bConnected == false)
		{
			bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			ba = bm.getAdapter();
			bd = ba.getRemoteDevice(addr);
			bg = bd.connectGatt(this, false, mGattCallback);
		
			bflag = bg.connect();
		
			if(bflag == true)
			{
				Log.d(TAG,"connect() is true");
			}
			else
			{
				Log.d(TAG,"connect() is false");
			}
		}
	}
	
	void step3(byte a)
	{
		Log.d(TAG,"step3");
		
		send_data = a;
		
		if(bConnected == false)
		{
			step1();
			return;
		}

		if(bg == null)
		{
			Log.d(TAG,"bg is null");
			return;
		}
				
		boolean bflag = bg.discoverServices();

		if(bflag != true)
		{
			Log.d(TAG,"discoverServices() is false");
		}
		
	}
	
	void step4()
	{
		Log.d(TAG,"step4");
		
		boolean bflag = false;
		
		List<BluetoothGattService> lists = bg.getServices();

		for(int i = 0 ; i < lists.size() ; i++)
		{
			BluetoothGattService service = lists.get(i);
			
			if(service.getUuid().equals(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")) == true)
			{
				BluetoothGattCharacteristic character = service.getCharacteristic(UUID.fromString("00003010-0000-1000-8000-00805f9b34fb"));
				
				if(character == null)
				{
					Log.d(TAG,"character is null");
					return;
				}
				
				bflag = bg.readCharacteristic(character);

				if(bflag == false)
				{
					Log.d(TAG,"readCharacter is false");
					return;
				}
			}
		}
	}
	
	void step5()
	{
		Log.d(TAG,"step5");
		
		boolean bflag = false;
		
		List<BluetoothGattService> lists = bg.getServices();

		for(int i = 0 ; i < lists.size() ; i++)
		{
			BluetoothGattService service = lists.get(i);
			
			if(service.getUuid().equals(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")) == true)
			{
				BluetoothGattCharacteristic character2 = service.getCharacteristic(UUID.fromString("00003010-0000-1000-8000-00805f9b34fb"));
				
				if(character2 == null)
				{
					Log.d(TAG,"character2 is null");
					return;
				}
				
				character2.setValue((byte)0x31,BluetoothGattCharacteristic.FORMAT_UINT8,0);
				character2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
				
				bflag = bg.writeCharacteristic(character2);

				if(bflag == false)
				{
					Log.d(TAG,"writeCharacter is false");
				}
			}
		}
	}
	
	void step6()
	{
		Log.d(TAG,"step6");
		
		boolean bflag = false;
		
		List<BluetoothGattService> lists = bg.getServices();

		for(int i = 0 ; i < lists.size() ; i++)
		{
			BluetoothGattService service = lists.get(i);
			
			if(service.getUuid().equals(UUID.fromString("0000ff00-0000-1000-8000-00805f9b34fb")) == true)
			{
				BluetoothGattCharacteristic character2 = service.getCharacteristic(UUID.fromString("00003012-0000-1000-8000-00805f9b34fb"));
				
				if(character2 == null)
				{
					Log.d(TAG,"character2 is null");
					return;
				}
				
				character2.setValue((byte)send_data,BluetoothGattCharacteristic.FORMAT_UINT8,0);
				character2.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
				
				bflag = bg.writeCharacteristic(character2);

				if(bflag == false)
				{
					Log.d(TAG,"writeCharacter is false");
				}
			}
		}
	}
}
