package com.areeb.filesharingapp.activities

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.areeb.filesharingapp.R
import com.areeb.filesharingapp.activities.MainActivity.Companion.BLUETOOTH_JSON_OBJECT
import com.areeb.filesharingapp.databinding.ActivityBluetoothDeviceDetailsBinding

class BluetoothDeviceDetails : AppCompatActivity() {

    private lateinit var binding: ActivityBluetoothDeviceDetailsBinding
    private lateinit var bluetoothDevice: BluetoothDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Bluetooth Device Detail"

        //initialize layout binding
        binding = ActivityBluetoothDeviceDetailsBinding.inflate(layoutInflater)

        //set up back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setContentView(binding.root)

        //get intent data
        if (intent != null) {

            bluetoothDevice = intent.getParcelableExtra(BLUETOOTH_JSON_OBJECT)!!

            //set address in text view
            binding.addressValueTextView.text = bluetoothDevice.address

            //set alias in text view
            binding.aliasValueTextView.text = bluetoothDevice.alias

            //set bluetooth class in text view
            when (bluetoothDevice.bluetoothClass.majorDeviceClass) {
                BluetoothClass.Device.Major.AUDIO_VIDEO -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.audio_video)
                }
                BluetoothClass.Device.Major.COMPUTER -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.computer)
                }
                BluetoothClass.Device.Major.HEALTH -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.health)
                }
                BluetoothClass.Device.Major.IMAGING -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.imaging)
                }
                BluetoothClass.Device.Major.MISC -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.misc)
                }
                BluetoothClass.Device.Major.NETWORKING -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.networking)
                }
                BluetoothClass.Device.Major.PERIPHERAL -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.peripherals)
                }
                BluetoothClass.Device.Major.PHONE -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.phone)
                }
                BluetoothClass.Device.Major.TOY -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.toy)
                }
                BluetoothClass.Device.Major.WEARABLE -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.wearable)
                }
                BluetoothClass.Device.Major.UNCATEGORIZED -> {
                    binding.bluetoothClassValueTextView.text = getString(R.string.uncategorized)
                }
            }

            //set bond state in text view
            when (bluetoothDevice.bondState) {
                BluetoothDevice.BOND_NONE -> {
                    binding.bondStateValueTextView.text = getString(R.string.bond_none)
                }
                BluetoothDevice.BOND_BONDING -> {
                    binding.bondStateValueTextView.text = getString(R.string.bond_bonding)
                }
                BluetoothDevice.BOND_BONDED -> {
                    binding.bondStateValueTextView.text = getString(R.string.bond_bonded)
                }
            }

            //set name in text view
            binding.nameValueTextView.text = bluetoothDevice.name

            //set type in text view
            when (bluetoothDevice.type) {
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> {
                    binding.typeValueTextView.text = getString(R.string.device_type_classic)
                }
                BluetoothDevice.DEVICE_TYPE_LE -> {
                    binding.typeValueTextView.text = getString(R.string.device_type_le)
                }
                BluetoothDevice.DEVICE_TYPE_DUAL -> {
                    binding.typeValueTextView.text = getString(R.string.device_type_dual)
                }
                BluetoothDevice.DEVICE_TYPE_UNKNOWN -> {
                    binding.typeValueTextView.text = getString(R.string.device_type_unknown)
                }
            }
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