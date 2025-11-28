package com.radovan.play.controllers

import com.radovan.play.services.InfractionService
import com.radovan.play.utils.ResponsePackage
import com.radovan.play.security.JwtSecuredAction
import jakarta.inject.Inject
import org.apache.hc.core5.http.HttpStatus
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

class InfractionController @Inject()(
                                      cc: ControllerComponents,
                                      infractionService: InfractionService,
                                      secured: JwtSecuredAction
                                    ) extends AbstractController(cc) {

  def getAllInfractions: Action[AnyContent] = secured { req =>
    new ResponsePackage(infractionService.listAll, HttpStatus.SC_OK).toResult
  }

  def getInfractionDetails(infractionId: Long): Action[AnyContent] = secured { req =>
    new ResponsePackage(infractionService.getInfractionById(infractionId), HttpStatus.SC_OK).toResult
  }

  def getInfractionsCount: Action[AnyContent] = secured { req =>
    new ResponsePackage(infractionService.countInfractions, HttpStatus.SC_OK).toResult
  }
}
