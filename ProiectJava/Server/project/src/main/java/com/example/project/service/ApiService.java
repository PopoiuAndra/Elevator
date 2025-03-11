package com.example.project.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

    private RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 5000)
    public void callApi() throws InterruptedException {
        // URMEAZA SA FIE IMPLEMENTATA LOGICA
        for(int i = 1; i <= 2; ++ i) {
            System.out.println("Elevator " + i);

            Integer currentFloor = restTemplate.getForObject("http://localhost:1520/elevator/floor/" + i, Integer.class);
            System.out.println("Floor he is on " + currentFloor);

            String direction = restTemplate.getForObject("http://localhost:1520/elevator/direction/" + i, String.class);
            System.out.println("Direction he is going " + direction);

            Integer shouldStop = restTemplate.getForObject("http://localhost:1520/elevator/shouldStop/" + i + "/" + currentFloor, Integer.class);
            System.out.println("Should he stop " + shouldStop);

            if(shouldStop == 1)
            {
                // STEGRE SHOULDSTOPUL DE LA CURRENT FLOOR PENTRU EL
                restTemplate.put("http://localhost:1520/elevator/update/dontStop/" + i + "/" + currentFloor, 0);
                System.out.println("Stop deleted");
            }
            Integer countStops = restTemplate.getForObject("http://localhost:1520/elevator/countStops/" + i, Integer.class);
            System.out.println("Number of stops " + countStops);



            switch (direction){
                case "UP":
                    if(countStops == 0){
                    // SCHIMBA DIRECTIA
                        restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/STOPPED", 0);
                        System.out.println("Direction updated");
                    }
                    else if(currentFloor < 5) {
                        // INCREMENTEAZA ETAJUL
                        restTemplate.put("http://localhost:1520/elevator/update/floor/" + i + "/" + (currentFloor + 1), 0);
                        System.out.println("Floor updated");
                    }
                    else {
                        // SCHIMBA DIRECTIA
                        restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/DOWN", 0);
                        System.out.println("Direction updated");
                    }
                    break;
                case "DOWN":
                    if(countStops == 0){
                        // SCHIMBA DIRECTIA
                        restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/STOPPED", 0);
                        System.out.println("Direction updated");
                    }
                    if(currentFloor > 0) {
                        // DECREMENTEAZA ETAJUL
                        restTemplate.put("http://localhost:1520/elevator/update/floor/" + i + "/" + (currentFloor - 1), 0);
                        System.out.println("Floor updated");
                    }
                    else {
                        // SCHIMBA DIRECTIA
                        restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/UP", 0);
                        System.out.println("Direction updated");
                    }
                    break;
                case "STOPPED":
                    if(countStops != 0){
                        // VEDEM UNDE A APARUT O OPRIRE
                        Integer firstStop = restTemplate.getForObject("http://localhost:1520/elevator/randomStop/" + i , Integer.class);
                        System.out.println("First stop " + firstStop);

                        if(firstStop > currentFloor)
                        {
                            // SCHIMBA DIRECTIA
                            restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/UP", 0);
                            System.out.println("Direction updated");
                        }
                        else if(firstStop < currentFloor)
                        {
                            // SCHIMBA DIRECTIA
                            restTemplate.put("http://localhost:1520/elevator/update/direction/" + i + "/DOWN", 0);
                            System.out.println("Direction updated");
                        }
                    }
                    break;
            }


        }
        System.out.println("APIs called");
    }
}