package com.radovan.spring.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.radovan.spring.brokers.RadarNatsSender;
import com.radovan.spring.converter.TempConverter;
import com.radovan.spring.dto.RadarDto;
import com.radovan.spring.entity.RadarEntity;
import com.radovan.spring.exceptions.InstanceUndefinedException;
import com.radovan.spring.repository.RadarRepository;
import com.radovan.spring.services.RadarService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@Service
public class RadarServiceImpl implements RadarService {

    private RadarRepository radarRepository;
    private TempConverter tempConverter;
    private RadarNatsSender natsSender;
    private ObjectMapper objectMapper;

    private final ScheduledExecutorService patrolScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> patrolFuture; // referenca na aktivnu patrolu
    private final Map<Long, Long> vehicleCooldown = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Autowired
    private void initialize(RadarRepository radarRepository, TempConverter tempConverter,
                            RadarNatsSender natsSender, ObjectMapper objectMapper) {
        this.radarRepository = radarRepository;
        this.tempConverter = tempConverter;
        this.natsSender = natsSender;
        this.objectMapper = objectMapper;
    }

    @Override
    public RadarDto addRadar(RadarDto radarDto) {
        RadarEntity storedRadar = radarRepository.save(tempConverter.dtoToEntity(radarDto));
        return tempConverter.entityToDto(storedRadar);
    }

    @Override
    public RadarDto getRadarById(Long radarId) {
        RadarEntity radarEntity = radarRepository.findById(radarId)
                .orElseThrow(() -> new InstanceUndefinedException("The radar has not been found!"));
        return tempConverter.entityToDto(radarEntity);
    }

    @Override
    public RadarDto updateRadar(RadarDto radarDto, Long radarId) {
        getRadarById(radarId);
        radarDto.setId(radarId);
        RadarEntity updatedRadar = radarRepository.save(tempConverter.dtoToEntity(radarDto));
        return tempConverter.entityToDto(updatedRadar);
    }

    @Override
    public void deleteRadar(Long radarId) {
        getRadarById(radarId);
        radarRepository.deleteById(radarId);
    }

    @Override
    public List<RadarDto> listAll() {
        return radarRepository.findAll().stream().map(tempConverter::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<RadarDto> listAllByName(String keyword) {
        return radarRepository.findAllByName(keyword).stream().map(tempConverter::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RadarDto> listAllActive() {
        return radarRepository.findAll().stream().map(tempConverter::entityToDto).collect(Collectors.toList());
    }

    @Override
    public void activateRadarPatrol() {
        List<RadarDto> allRadars = listAllActive();
        JsonNode[] allVehicles = natsSender.retrieveAllVehicles();

        // Sačuvaj referencu na zakazani task
        patrolFuture = patrolScheduler.scheduleAtFixedRate(() -> {
            try {
                JsonNode vehicleNode = allVehicles[random.nextInt(allVehicles.length)];
                long vehicleId = vehicleNode.get("id").longValue();

                long now = System.currentTimeMillis();
                long lastSeen = vehicleCooldown.getOrDefault(vehicleId, 0L);
                if (now - lastSeen < (3 * 60 * 1000)) {
                    System.out.println("⏳ Vehicle " + vehicleNode.get("registrationNumber").stringValue() + " is on cooldown.");
                    return;
                }

                RadarDto radar = allRadars.get(random.nextInt(allRadars.size()));
                long maxSpeed = radar.getMaxSpeed();

                int speed;
                if (random.nextDouble() < 0.9) {
                    long minSpeed = Math.max(30, maxSpeed - 20);
                    long range = maxSpeed - minSpeed + 1;
                    speed = (int) (minSpeed + random.nextInt((int) range));
                } else {
                    int range = 40;
                    speed = (int) (maxSpeed + 1 + random.nextInt(range));
                }

                System.out.printf("📸 Radar %s captured vehicle %s (%s) at %d km/h\n",
                        radar.getName(),
                        vehicleNode.get("registrationNumber").stringValue(),
                        vehicleNode.get("brand").stringValue(),
                        speed);

                if (speed > maxSpeed) {
                    System.out.printf("⚡ Infraction committed! Vehicle %s exceeded speed limit (limit=%d, speed=%d km/h)\n",
                            vehicleNode.get("registrationNumber").stringValue(), maxSpeed, speed);

                    ObjectNode infractionJson = objectMapper.createObjectNode();
                    infractionJson.put("vehicleRegistrationNumber", vehicleNode.get("registrationNumber").stringValue());
                    infractionJson.put("vehicleSpeed", speed);
                    infractionJson.put("radarId", radar.getId());

                    natsSender.sendInfraction(infractionJson);
                }

                vehicleCooldown.put(vehicleId, now);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public void deactivateRadarPatrol() {
        if (patrolFuture != null && !patrolFuture.isCancelled()) {
            patrolFuture.cancel(true); // prekida task
            System.out.println("🛑 Radar patrol deactivated.");
        }
    }

    @Override
    public void activateRadar(Long radarId) {
        RadarDto radar = getRadarById(radarId);
        radar.setStatus((short) 1);
        addRadar(radar);
    }

    @Override
    public void deactivateRadar(Long radarId) {
        RadarDto radar = getRadarById(radarId);
        radar.setStatus((short) 0);
        addRadar(radar);
    }
}
