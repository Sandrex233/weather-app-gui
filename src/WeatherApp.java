import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class WeatherApp extends JFrame {
    private JLabel cityLabel;
    private JTextField cityTextField;
    private JButton searchButton;
    private JTextArea weatherTextArea;
    private JLabel temperatureLabel;
    private JLabel humidityLabel;
    private JLabel windSpeedLabel;

    String apiKey = System.getenv("WEATHER_API_KEY");
    private String baseUrl = "https://api.weatherapi.com/v1/current.json?key=";

    public WeatherApp() {
        // Set up the UI components
        cityLabel = new JLabel("City:");
        cityTextField = new JTextField(20);
        searchButton = new JButton("Search");
        weatherTextArea = new JTextArea(10, 20);

        // Create new labels to display weather information
        temperatureLabel = new JLabel("Temperature:");
        humidityLabel = new JLabel("Humidity:");
        windSpeedLabel = new JLabel("Wind Speed:");


        // Set up the layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        // Add the city label and text field
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        panel.add(cityLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(cityTextField, constraints);

        // Add the search button
        constraints.gridx = 2;
        constraints.gridy = 0;
        panel.add(searchButton, constraints);

        // Add the temperature label and text field
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(temperatureLabel, constraints);

        // Add the humidity label and text field
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(humidityLabel, constraints);

        // Add the wind speed label and text field
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(windSpeedLabel, constraints);

        // Add the weather text area
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 3;
        panel.add(new JScrollPane(weatherTextArea), constraints);

        // Set up the event listener for the search button
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String city = cityTextField.getText();
                getWeather(city);
            }
        });

        // Add the panel to the JFrame
        add(panel);

        // Set up the JFrame
        setTitle("Weather App");
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void getWeather(String city) {
        try {
            URL url = new URL(baseUrl + apiKey + "&q=" + city);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            StringBuffer response = null;
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject location = jsonResponse.getJSONObject("location");
                JSONObject current = jsonResponse.getJSONObject("current");

                // Extract the values of interest from the JSON response
                double temperature = current.getDouble("temp_c");
                int humidity = current.getInt("humidity");
                double windSpeed = current.getDouble("wind_kph");
                String locationName = location.getString("name");
                String region = location.getString("region");
                String country = location.getString("country");

                reader.close();

                // Update the UI with the new weather information
                temperatureLabel.setText("Temperature: " + temperature + "°C");
                humidityLabel.setText("Humidity: " + humidity + "%");
                windSpeedLabel.setText("Wind Speed: " + windSpeed + "km/h");

                weatherTextArea.setText(response.toString());
            } else {
                weatherTextArea.setText("Error: " + responseCode);
                JOptionPane.showMessageDialog(null, "Error message goes here \n" + "Error: " + responseCode, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        WeatherApp app = new WeatherApp();
        System.out.println(System.getenv("WEATHER_API_KEY"));
    }
}