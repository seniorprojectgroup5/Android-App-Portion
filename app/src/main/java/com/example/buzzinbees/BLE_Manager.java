package com.example.buzzinbees;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BLE_Manager extends Fragment {
    private static final String TAG = "Bluetooth Manager";

    MainActivity main;
    private OnFragmentInteractionListener mListener;

    private UUID bleDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String DEVICE_LIST = "temp device list";
    private static final String DEVICE_LIST_SELECTED = "temp device selected";
    private ImageButton searchBtn,  connectBtn;
    private ListView lView;
    private BluetoothAdapter bleAdapter;

    private Handler mHandler;

    private ImageView D_Status;

    //** Fragment Set up **//
    public BLE_Manager() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble__manager, null);
        mHandler = new Handler();

        // if we know nothing we need to start from scratch, else get the stuff we know from the phone
        lView = view.findViewById(R.id.listview);
        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) lView.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    connectBtn.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }
        }
        else {
            initList(new ArrayList<BluetoothDevice>());
        }

        D_Status = view.findViewById(R.id.bleManagerDeviceStatus);


        searchBtn = view.findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bleAdapter = BluetoothAdapter.getDefaultAdapter();

                searchBtn.setColorFilter(Color.parseColor("#93a4a9"));
                mHandler.postDelayed(rstSearchBtn,100);

                if (bleAdapter == null) {
                    Toast.makeText(getContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                }
                else if (!bleAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, Constant.BT_ENABLE_REQUEST);
                }
                else {
                    new SearchDevices().execute();
                }
            }
        });

        connectBtn = view.findViewById(R.id.connect);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                BluetoothDevice device = ((MyAdapter) (lView.getAdapter())).getSelectedItem();
                mListener.setUpBluetooth(device, bleDeviceUUID.toString());

                connectBtn.setColorFilter(Color.parseColor("#93a4a9"));
                mHandler.postDelayed(rstConnectBtn,100);
                mHandler.postDelayed(checkBleConnection, 1000);
            }
        });


        main = (MainActivity) getActivity();


        mHandler.post(checkBleConnection);

        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //runnable stuff
    final Runnable rstSearchBtn = new Runnable() {
        @Override
        public void run() {
            searchBtn.setColorFilter(Color.parseColor("#21252f"));

        }
    };
    final Runnable rstConnectBtn = new Runnable() {
        @Override
        public void run() {
            connectBtn.setColorFilter(Color.parseColor("#21252f"));
        }
    };
    final Runnable checkBleConnection = new Runnable() {
        @Override
        public void run() {
            if(main.mIsBluetoothConnected){
                D_Status.setImageResource(R.drawable.ic_queenbeetopdown_fill);
            }else{
                D_Status.setImageResource(R.drawable.ic_queenbeetopdown_clear);
            }
        }
    };


    // Get all the BLE devices
    //      prepares the list for the UI
    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getContext(), R.layout.ble_view, R.id.lstContent, objects);
        lView.setAdapter(adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                connectBtn.setEnabled(true);
            }
        });
    }
    //      looks for paired devices inside the users' phone
    private class SearchDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {
        // gets all the paired devices (the devices that are stored on the device)
        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = bleAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices;
        }

        // catches the "no devices found" problem
        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            if (listDevices.size() > 0) {
                MyAdapter adapter = (MyAdapter) lView.getAdapter();
                adapter.replaceItems(listDevices);
            } else {
                Toast.makeText(getContext(), "No paired devices found, please pair your serial BT device and try again", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //      this is a beefy adapter that helps with the construction of the device list
    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
        private int selectedIndex;
        private Context context;
        private List<BluetoothDevice> myList;

        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(ctx, resource, textViewResourceId, objects);
            context = ctx;
            myList = objects;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int position) {
            selectedIndex = position;
            notifyDataSetChanged();
        }

        public BluetoothDevice getSelectedItem() {
            return myList.get(selectedIndex);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView tv;
        }

        public void replaceItems(List<BluetoothDevice> list) {
            myList = list;
            notifyDataSetChanged();
        }

        public List<BluetoothDevice> getEntireList() {
            return myList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if (convertView == null) {
                vi = LayoutInflater.from(context).inflate(R.layout.ble_view, null);
                holder = new ViewHolder();

                holder.tv = vi.findViewById(R.id.lstContent);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                holder.tv.setBackgroundColor(Color.parseColor("#d9a827"));
                holder.tv.setTextColor(Color.parseColor("#21252F"));
            } else {
                holder.tv.setBackgroundColor(Color.TRANSPARENT);
                holder.tv.setTextColor(Color.parseColor("#e1e1e1"));
            }

            BluetoothDevice device = myList.get(position);
            holder.tv.setText(device.getName());

            return vi;
        }
    }
}
