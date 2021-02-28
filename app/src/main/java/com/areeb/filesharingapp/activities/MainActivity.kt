package com.areeb.filesharingapp.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.areeb.filesharingapp.R
import com.areeb.filesharingapp.databinding.ActivityMainBinding

private const val REQUEST_ENABLE_BLUETOOTH = 100
private const val PICK_FILE = 101

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var builder: AlertDialog.Builder
    var bluetoothDevices = mutableListOf<String>()
    var deviceAddresses = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var selectedDeviceAddress: String

    companion object {
        const val BLUETOOTH_JSON_OBJECT: String = "BLUETOOTH_JSON_OBJECT"
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String = intent?.action.toString()
            Log.d("Areeb", action)

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                //set text in status text view
                binding.statusTextView.text = getString(R.string.finished)

                //disable search button
                binding.searchBluetoothButton.isEnabled = true
                binding.searchBluetoothButton.isClickable = true

            } else if (BluetoothDevice.ACTION_FOUND == action) {

                val bluetoothDevice =
                    intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = bluetoothDevice?.name
                val address = bluetoothDevice?.address
                val rssi =
                    intent?.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toString()

                if (!deviceAddresses.contains(address)) {
                    deviceAddresses.add(address!!)

                    val deviceString = if (deviceName == null || deviceName.isEmpty()) {
                        "$address -  RSSI $rssi dB"
                    } else {
                        "$deviceName -  RSSI $rssi dB"
                    }

                    bluetoothDevices.add(deviceString)

                    arrayAdapter.notifyDataSetChanged()
                }

            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {

                val state = intent?.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                if (state == BluetoothAdapter.STATE_ON) {
                    //bluetoothAdapter.startDiscovery()
                    searchBluetoothDevice(binding.searchBluetoothButton)
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    Log.d("Areeb", "State OFF")
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                when (intent?.getIntExtra(
                    BluetoothDevice.EXTRA_BOND_STATE,
                    BluetoothAdapter.ERROR
                )) {
                    BluetoothDevice.BOND_BONDING -> {
                        Toast.makeText(this@MainActivity, "Connecting...", Toast.LENGTH_SHORT)
                            .show()
                    }
                    BluetoothDevice.BOND_BONDED -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Connected successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    BluetoothDevice.BOND_NONE -> {
                        Toast.makeText(this@MainActivity, "Connection failed", Toast.LENGTH_LONG)
                            .show()
                    }
                    BluetoothAdapter.ERROR -> {
                        Toast.makeText(this@MainActivity, "Connection failed", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initialize array adapter
        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_expandable_list_item_1,
            bluetoothDevices
        )

        //set array adapter to list view
        binding.bluetoothDeviceListView.adapter = arrayAdapter

        //initialize Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //register broadcast receiver for bluetooth
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(broadCastReceiver, intentFilter)

        //list view onClick listener
        binding.bluetoothDeviceListView.setOnItemClickListener { _, _, position, _ ->
            if (bluetoothAdapter.isEnabled) {

                //get remote device
                if (BluetoothAdapter.checkBluetoothAddress(deviceAddresses[position])) {
                    val bluetoothDevice: BluetoothDevice = bluetoothAdapter.getRemoteDevice(
                        deviceAddresses[position]
                    )

                    val deviceName = bluetoothDevices[position]

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Bluetooth Device")
                    builder.setMessage("Connect to Bluetooth device $deviceName")

                    //performing positive action
                    builder.setPositiveButton("Connect") { _, _ ->
                        //create bond with remote device
                        if (bluetoothDevice.createBond()) {
                            Log.d("Areeb", "Connection started")
                        } else {
                            when (bluetoothDevice.bondState) {
                                BluetoothDevice.BOND_NONE -> {
                                    Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                BluetoothDevice.BOND_BONDING -> {
                                    Toast.makeText(this, "Connecting..", Toast.LENGTH_SHORT).show()
                                }
                                BluetoothDevice.BOND_BONDED -> {
                                    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    //performing cancel action
                    builder.setNeutralButton("View") { _, _ ->

                        val intent = Intent(this, BluetoothDeviceDetails::class.java)
                        intent.putExtra(BLUETOOTH_JSON_OBJECT, bluetoothDevice)
                        startActivity(intent)
                    }

                    //performing negative action
                    builder.setNegativeButton("Cancel") { _, _ -> }

                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)

                    //show alert dialog
                    alertDialog.show()
                } else {
                    Toast.makeText(this, "Invalid MAC Address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enable Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bluetoothDeviceListView.setOnItemLongClickListener { _, _, pos, _ ->

            selectedDeviceAddress = deviceAddresses[pos]

            val intent = Intent(ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            intent.type = "application/*"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(
                    Intent.createChooser(intent, "Choose file"),
                    PICK_FILE
                )
            } else {
                Log.d("Error", "Unable to resolve Intent.ACTION_OPEN_DOCUMENT {}")
            }

            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val title = item.title

        if (title == getString(R.string.paired_devices)) {
            val intent = Intent(this, PairedDevicesActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


    //search bluetooth device onClick method
    fun searchBluetoothDevice(view: View) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startSearchingDevice()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_DENIED
            ) {

                //initialize alert dialog
                builder = AlertDialog.Builder(this)
                builder.setMessage("Location permission required to share files via Bluetooth")
                builder.setCancelable(false)
                builder.setPositiveButton("Ok") { _, _ ->
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }

                //show alert dialog
                val alertDialog = builder.create()
                alertDialog.setTitle("Location Permission")
                alertDialog.show()
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startSearchingDevice()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startSearchingDevice()
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    //method to search bluetooth device
    private fun startSearchingDevice() {
        //set text in status text view
        binding.statusTextView.text = getString(R.string.searching)

        //disable search button
        binding.searchBluetoothButton.isEnabled = false
        binding.searchBluetoothButton.isClickable = false

        //clear previous detected devices
        bluetoothDevices.clear()
        deviceAddresses.clear()

        //discover bluetooth devices
        if (bluetoothAdapter.isEnabled)
            bluetoothAdapter.startDiscovery()
        else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == 0) {
                //set text in status text view
                binding.statusTextView.text = ""

                //disable search button
                binding.searchBluetoothButton.isEnabled = true
                binding.searchBluetoothButton.isClickable = true
            }
        } else if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                if (data?.data != null) {

                    //get file uri
                    val fileUri = data.data

                    val sharingIntent = Intent(
                        Intent.ACTION_SEND
                    )
                    sharingIntent.type = "*/*"
                    sharingIntent.component = ComponentName(
                        "com.android.bluetooth",
                        "com.android.bluetooth.opp.BluetoothOppLauncherActivity"
                    )
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
                    startActivity(sharingIntent)

                } else {
                    Log.d("Areeb", "File uri not found {}")
                }
            } else {
                Log.d("Areeb", "User cancelled file browsing {}")
            }
        }
    }
}