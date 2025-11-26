package com.radovan.play.controllers;

import com.radovan.play.dto.OwnerDto;
import com.radovan.play.exceptions.DataNotValidatedException;
import com.radovan.play.security.JwtAuthAction;
import com.radovan.play.services.OwnerService;
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
public class OwnerController extends Controller {

    private OwnerService ownerService;
    private FormFactory formFactory;

    @Inject
    private void initialize(OwnerService ownerService, FormFactory formFactory) {
        this.ownerService = ownerService;
        this.formFactory = formFactory;
    }

    public Result getAllOwners(){
        return ok(Json.toJson(ownerService.listAll()));
    }

    public Result getOwnerDetails(Long ownerId){
        return ok(Json.toJson(ownerService.getOwnerById(ownerId)));
    }

    public Result saveOwner(Http.Request request){
        Form<OwnerDto> ownerForm = formFactory.form(OwnerDto.class).bindFromRequest(request);
        if(ownerForm.hasErrors()){
            throw new DataNotValidatedException("Owner form has not been validated!");
        }

        OwnerDto storedOwner = ownerService.addOwner(ownerForm.get());
        return created(Json.toJson("Owner with id " + storedOwner.getId() + " has been stored!"));
    }

    public Result updateOwner(Http.Request request,Long ownerId){
        Form<OwnerDto> ownerForm = formFactory.form(OwnerDto.class).bindFromRequest(request);
        if(ownerForm.hasErrors()){
            throw new DataNotValidatedException("Owner form has not been validated!");
        }

        OwnerDto updatedOwner = ownerService.updateOwner(ownerForm.get(),ownerId);
        return ok(Json.toJson("Owner with id " + updatedOwner.getId() + " has been updated!"));
    }

    public Result countOwners(){
        return ok(Json.toJson(ownerService.count()));
    }

    public Result deleteOwner(Long ownerId, Http.Request request){
        ownerService.deleteOwner(ownerId, TokenUtils.provideToken(request));
        return ok(Json.toJson("Owner with id " + ownerId + " has been permanently removed!"));
    }
}
