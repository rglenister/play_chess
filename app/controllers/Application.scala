package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Redirect(routes.Application.chess)
  }
  
  def chess = Action {
    Ok(views.html.index("A String"))  
  }
  
}