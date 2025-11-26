package com.radovan.play.controllers;

import com.radovan.play.dto.VehicleDto;
import com.radovan.play.exceptions.DataNotValidatedException;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.services.VehicleService;
import com.radovan.play.utils.TokenUtils;
import jakarta.inject.Inject;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

@With(JwtAuthAction.class)
public class VehicleController extends Controller {

    private VehicleService vehicleService;
    private FormFactory formFactory;

    @Inject
    private void initialize(VehicleService vehicleService, FormFactory formFactory) {
        this.vehicleService = vehicleService;
        this.formFactory = formFactory;
    }

    public Result getAllVehicles(){
        return ok(Json.toJson(vehicleService.listAll()));
    }

    public Result getAllByKeyword(String keyword){
        return ok(Json.toJson(vehicleService.listAllByRegistrationNumberContains(keyword)));
    }

    public Result getVehicleDetails(Long vehicleId){
        return ok(Json.toJson(vehicleService.getVehicleById(vehicleId)));
    }

    public Result getVehicleDetailsByRN(String registrationNumber){
        return ok(Json.toJson(vehicleService.getVehicleByRegistrationNumber(registrationNumber)));
    }

    public Result saveVehicle(Http.Request request){
        Form<VehicleDto> vehicleForm = formFactory.form(VehicleDto.class).bindFromRequest(request);
        if(vehicleForm.hasErrors()){
            throw new DataNotValidatedException("Vehicle form has not been validated!");
        }

        VehicleDto storedVehicle = vehicleService.addVehicle(vehicleForm.get());
        return created(Json.toJson("Vehicle with id " + storedVehicle.getId() + " has been stored!"));
    }

    public Result updateVehicle(Http.Request request,Long vehicleId){
        Form<VehicleDto> vehicleForm = formFactory.form(VehicleDto.class).bindFromRequest(request);
        if(vehicleForm.hasErrors()){
            throw new DataNotValidatedException("Vehicle form has not been validated!");
        }

        VehicleDto updatedVehicle = vehicleService.updateVehicle(vehicleForm.get(), vehicleId);
        return ok(Json.toJson("Vehicle with id " + updatedVehicle.getId() + " has been updated!"));
    }

    public Result deleteVehicle(Long vehicleId, Http.Request request){
        vehicleService.deleteVehicle(vehicleId, TokenUtils.provideToken(request));
        return ok(Json.toJson("Vehicle with id " + vehicleId + " has been permanently removed!"));
    }

    public Result countVehicles(){
        return ok(Json.toJson(vehicleService.vehicleCount()));
    }

    public Result getAllByOwnerId(Long ownerId){
        return ok(Json.toJson(vehicleService.listAllByOwnerId(ownerId)));
    }


}
