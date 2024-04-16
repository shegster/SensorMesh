package com.projektarbeit.sensormesh.controller;

import java.util.ArrayList;
import com.projektarbeit.sensormesh.models.CCSSensor;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class CCSController extends SensorControl<CCSSensor>{

    @FXML
    private TextArea co2Box;
    @FXML
    private LineChart<Integer, Integer> co2Chart;
    @FXML
    private NumberAxis co2XAxis;
    @FXML
    private NumberAxis co2YAxis;
    @FXML
    private NumberAxis tVocXAxis;
    @FXML
    private NumberAxis tVocYAxis;
    @FXML
    private TextArea delayBox;
    @FXML
    private Label devId;
    @FXML
    private Label devName;
    @FXML
    private TextArea tvocBox;
    @FXML
    private LineChart<Integer, Integer> tvocChart;
    @FXML
    private Font x1;
    @FXML
    private Font x11;
    @FXML
    private Font x12;

    private final ArrayList<Integer> co2Data = new ArrayList<>();
    private final ArrayList<Integer> tVocData = new ArrayList<>();

    @FXML
    void initialize() {
        //Initialisierung der Oberfläche
        configureChartAxes();
    }

    @Override
    public void update(){
        //Aufruf zum updaten der Oberfläche
        Platform.runLater(() -> {
            devName.setText(model.getName());
            devId.setText(Long.toString(model.getId()));
            co2Box.setText(model.geteCO2()+" ppm");
            tvocBox.setText(model.gettVoc()+" ppb");
            delayBox.setText(model.getDelayMillis() + " ms");

            fetchListData(model.geteCO2(),co2Data);
            fetchListData(model.gettVoc(),tVocData);

            updateLineChart(co2Chart,co2Data);
            updateLineChart(tvocChart,tVocData);

            System.out.println("UPDATE!!!!");
        });
    }

    private void updateLineChart(LineChart<Integer,Integer> chart, ArrayList<Integer> data){
        //Updaten der Darstellung der Line-Chart
        Platform.runLater(() -> {

            if(chart.getData().isEmpty()){
                XYChart.Series<Integer, Integer> series = new XYChart.Series<>();
                for(int index = 0; index < data.size(); index++){
                    series.getData().add(new XYChart.Data<>(index,data.get(index)));
                }
                chart.getData().add(series);
            }else {
                XYChart.Series<Integer, Integer> series = chart.getData().getFirst();
                series.getData().clear();
                for(int index = 0; index < data.size(); index++){
                    series.getData().add(new XYChart.Data<>(index,data.get(index)));
                }
                chart.layout();
            }


        });
    }

    private void fetchListData(int data,ArrayList<Integer> dataList){
        //Array mit Daten aus dem Modell befüllen
        if(dataList.size()>60) dataList.removeFirst();
        dataList.add(data);
    }

    private void configureChartAxes() {
        //Initiale Konfiguration der Charts
        co2Chart.setCreateSymbols(false);
        co2Chart.setAnimated(false);
        co2XAxis.setAutoRanging(false);
        co2XAxis.setLowerBound(0);
        co2XAxis.setUpperBound(59); // Da wir bei 0 anfangen (60 Punkte insgesamt)
        co2XAxis.setTickUnit(1);

        co2YAxis.setAutoRanging(false);
        co2YAxis.setLowerBound(350);
        co2YAxis.setUpperBound(2000);

        tvocChart.setCreateSymbols(false);
        tvocChart.setAnimated(false);
        tVocXAxis.setAutoRanging(false);
        tVocXAxis.setLowerBound(0);
        tVocXAxis.setUpperBound(59); // Da wir bei 0 anfangen (60 Punkte insgesamt)
        tVocXAxis.setTickUnit(1);

        tVocYAxis.setAutoRanging(false);
        tVocYAxis.setLowerBound(0);
        tVocYAxis.setUpperBound(500);
    }
}

