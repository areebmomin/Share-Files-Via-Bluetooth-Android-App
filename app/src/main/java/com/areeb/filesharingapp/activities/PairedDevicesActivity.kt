package com.areeb.filesharingapp.activities

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.areeb.filesharingapp.databinding.ActivityPairedDevicesBinding

class PairedDevicesActivity : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var binding: ActivityPairedDevicesBinding
    private var bluetoothDevices = mutableListOf<BluetoothDevice>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var bluetoothDevicesName = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Paired Devices"

        //set up back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //inflate binding with current activity
        binding = ActivityPairedDevicesBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //initialize array adapter
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, bluetoothDevicesName)

        //set array adapter to list view
        binding.pairedDevicesListView.adapter = arrayAdapter

        //initialize bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //list view onClick listener
        binding.pairedDevicesListView.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, bluetoothDevices[position].address, Toast.LENGTH_LONG).show()
        }

        //check for bluetooth state
        if (bluetoothAdapter.isEnabled) {
            val bluetoothDevicesSet = bluetoothAdapter.bondedDevices

            bluetoothDevices = bluetoothDevicesSet.toList() as MutableList<BluetoothDevice>

            bluetoothDevices.forEach {
                bluetoothDevicesName.add(it.name)
            }

            arrayAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Turn on Bluetooth to view list", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}