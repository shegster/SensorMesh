package com.projektarbeit.sensormesh.controller;


import java.util.ArrayList;
import com.projektarbeit.sensormesh.models.BMPSensor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class BMPController extends SensorControl<BMPSensor> {

    @FXML
    private TextArea delayBox;
    @FXML
    private Label devId;
    @FXML
    private Label devName;
    @FXML
    private TextArea presBox;
    @FXML
    private LineChart<Integer, Double> presChart;
    @FXML
    private NumberAxis presXAxis;
    @FXML
    private NumberAxis presYAxis;
    @FXML
    private TextArea tempBox;
    @FXML
    private LineChart<Integer, Double> tempChart;
    @FXML
    private NumberAxis tempXAxis;
    @FXML
    private NumberAxis tempYAxis;
    @FXML
    private Font x1;
    @FXML
    private Font x11;
    @FXML
    private Font x12;

    private final ArrayList<Double> presData = new ArrayList<>();
    private final ArrayList<Double> tempData = new ArrayList<>();

    @FXML
    void initialize() {
        // Initialisierunge der Oberfläche
        configureChartAxes();
    }

    @Override
    public void update(){
        //Updaten der Oberfläche
        Platform.runLater(() -> {
            devName.setText(model.getName());
            devId.setText(Long.toString(model.getId()));
            tempBox.setText(model.getTemp()+" °C");
            presBox.setText(model.getPres()+" hPa");
            delayBox.setText(model.getDelayMillis() + " ms");

            fetchListData(model.getPres(),presData);
            fetchListData(model.getTemp(),tempData);

            updateLineChart(presChart,presData);
            updateLineChart(tempChart,tempData);

            System.out.println("UPDATE!!!!");
        });
    }

    private void updateLineChart(LineChart<Integer,Double> chart, ArrayList<Double> data){
        //Darstellung in Line Chart updaten
        Platform.runLater(() -> {

            if(chart.getData().isEmpty()){
                XYChart.Series<Integer, Double> series = new XYChart.Series<>();
                for(int index = 0; index < data.size(); index++){
                    series.getData().add(new XYChart.Data<>(index,data.get(index)));
                }
                chart.getData().add(series);
            }else {
                XYChart.Series<Integer, Double> series = chart.getData().getFirst();
                series.getData().clear();
                for(int index = 0; index < data.size(); index++){
                    series.getData().add(new XYChart.Data<>(index,data.get(index)));
                }
                chart.layout();
            }


        });
    }

    private void fetchListData(double data,ArrayList<Double> dataList){
        //Datenarrays mit daten auus modell befüllen
        if(dataList.size()>60) dataList.removeFirst();
        dataList.add(data);
    }

    private void configureChartAxes() {
        //Einstiegskonfiguration der Charts
        presChart.setCreateSymbols(false);
        presChart.setAnimated(false);
        presXAxis.setAutoRanging(false);
        presXAxis.setLowerBound(0);
        presXAxis.setUpperBound(59); // Da wir bei 0 anfangen (60 Punkte insgesamt)
        presXAxis.setTickUnit(1);

        presYAxis.setAutoRanging(false);
        presYAxis.setLowerBound(900);
        presYAxis.setUpperBound(1100);

        tempChart.setCreateSymbols(false);
        tempChart.setAnimated(false);
        tempXAxis.setAutoRanging(false);
        tempXAxis.setLowerBound(0);
        tempXAxis.setUpperBound(59); // Da wir bei 0 anfangen (60 Punkte insgesamt)
        tempXAxis.setTickUnit(1);

        tempYAxis.setAutoRanging(false);
        tempYAxis.setLowerBound(0);
        tempYAxis.setUpperBound(50);
    }
}

