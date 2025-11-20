package com.radovan.play.utils;

import com.radovan.play.converter.TempConverter;
import com.radovan.play.dto.OwnerDto;
import com.radovan.play.dto.VehicleDto;
import com.radovan.play.services.OwnerService;
import com.radovan.play.services.VehicleService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class LoadData {

    private OwnerService ownerService;
    private VehicleService vehicleService;
    private TempConverter tempConverter;

    @Inject
    private void initialize(OwnerService ownerService, VehicleService vehicleService, TempConverter tempConverter) {
        this.ownerService = ownerService;
        this.vehicleService = vehicleService;
        this.tempConverter = tempConverter;
        generateOwners();
        generateVehicles();
    }

    /*
    public void generateOwners() {
        if (ownerService.count() == 0L) {
            List<OwnerDto> owners = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("owners.txt"))))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String name = parts[0].trim();
                        String birthDateStr = parts[1].trim();
                        String email = parts[2].trim();
                        owners.add(new OwnerDto(name, birthDateStr, email));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            owners.forEach(ownerService::addOwner);
            System.out.println("Inserted " + owners.size() + " owners from conf/owners.txt");
        } else {
            System.out.println("Owners already exist, skipping seeding.");
        }
    }

     */


    public void generateOwners() {
        if (ownerService.count() == 0L) {
            try (Stream<String> lines = Files.lines(Paths.get("conf/owners.txt"))) {
                List<OwnerDto> owners = lines
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length == 3)
                        .map(parts -> new OwnerDto(parts[0].trim(), parts[1].trim(), parts[2].trim()))
                        .toList();

                owners.forEach(ownerService::addOwner);
                System.out.println("Inserted " + owners.size() + " owners from conf/owners.txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Owners already exist, skipping seeding.");
        }
    }

    public void generateVehicles() {
        if (vehicleService.vehicleCount() == 0L) {
            try (Stream<String> lines = Files.lines(Paths.get("conf/vehicles.txt"))) {

                // lista svih ownerId iz baze
                List<Long> ownerIds = ownerService.listAll()
                        .stream()
                        .map(OwnerDto::getId)
                        .toList();

                Random random = new Random();

                List<VehicleDto> vehicles = lines
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length == 4)
                        .map(parts -> {
                            String registrationNumber = parts[0].trim();
                            String brand = parts[1].trim();
                            Double fiscalPower = Double.valueOf(parts[2].trim());
                            String model = parts[3].trim();

                            // random ownerId iz liste
                            Long ownerId = ownerIds.get(random.nextInt(ownerIds.size()));

                            return new VehicleDto(registrationNumber, brand, fiscalPower, model, ownerId);
                        })
                        .toList();

                vehicles.forEach(vehicleService::addVehicle);
                System.out.println("Inserted " + vehicles.size() + " vehicles from conf/vehicles.txt");

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Vehicles already exist, skipping seeding.");
        }
    }

}
