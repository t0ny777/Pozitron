package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class ProductList extends AppCompatActivity {

    private CardView solar_panel, combiner_box;
    private CardView solar_charge_controller, battery;
    private CardView inverter, dc_and_ac_disconnects;
    private CardView miscellaneous_components;
    private CardView generator;
    private CardView electrical_motor, control_units;
    private CardView power_converter, gear_box;
    private CardView brakes;
    private CardView wind_turbine_installation, solar_panel_installation;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        solar_panel = findViewById(R.id.solar_panel);
        combiner_box = findViewById(R.id.combiner_box);
        solar_charge_controller = findViewById(R.id.solar_charge_controller);
        battery = findViewById(R.id.battery);
        inverter = findViewById(R.id.inverter);
        dc_and_ac_disconnects = findViewById(R.id.dc_and_ac_disconnects);
        miscellaneous_components = findViewById(R.id.miscellaneous_components);
        generator = findViewById(R.id.generator);
        electrical_motor = findViewById(R.id.electrical_motor);
        control_units = findViewById(R.id.control_units);
        power_converter = findViewById(R.id.power_converter);
        gear_box = findViewById(R.id.gear_box);
        brakes = findViewById(R.id.brakes);
        wind_turbine_installation = findViewById(R.id.wind_turbine_installation);
        solar_panel_installation =findViewById(R.id.solar_panel_installation);

//



        solar_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, SolarPanel.class);
                intent.putExtra("category", "Solar Panel");
                startActivity(intent);
            }
        });
        combiner_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, CombinerBoxActivity.class);
                intent.putExtra("category", "Combiner Box");
                startActivity(intent);
            }
        });
        solar_charge_controller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, SolarChargeController.class);
                intent.putExtra("category", "Solar Charge Controller");
                startActivity(intent);
            }
        });
        battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, AddNewProduct.class);
                intent.putExtra("category", "Battery");
                startActivity(intent);
            }
        });
        inverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, SolarInverter.class);
                intent.putExtra("category", "Inverter");
                startActivity(intent);
            }
        });
        dc_and_ac_disconnects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, DCandAC.class);
                intent.putExtra("category", "DC and AC Disconnects");
                startActivity(intent);
            }
        });
        miscellaneous_components.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, Brakes.class);
                intent.putExtra("category", "Wires");
                startActivity(intent);

            }
        });
        generator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, Generator.class);
                intent.putExtra("category", "Generator");
                startActivity(intent);
            }
        });
        electrical_motor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, ElectricalMotor.class);
                intent.putExtra("category", "Electrical Motor");
                startActivity(intent);
            }
        });
        control_units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, SolarChargeController.class);
                intent.putExtra("category", "Wind Turbine Controller");
                startActivity(intent);
            }
        });
        power_converter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, PowerConverter.class);
                intent.putExtra("category", "Power Converter");
                startActivity(intent);
            }
        });
        gear_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, GearBox.class);
                intent.putExtra("category", "Gear Box");
                startActivity(intent);
            }
        });
        brakes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, Brakes.class);
                intent.putExtra("category", "Other Details");
                startActivity(intent);
            }
        });

        wind_turbine_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, JobOfferJobSearch.class);
                intent.putExtra("category", "Wind_Turbine Installation");
                startActivity(intent);
            }
        });
        solar_panel_installation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductList.this, JobOfferJobSearch.class);
                intent.putExtra("category", "Solar_Panel Installation");
                startActivity(intent);
            }
        });

            }


}

