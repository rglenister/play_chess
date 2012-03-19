package controllers

import play.api._
import play.api.mvc._

import chess.GamePosition;
import chess.codec.FENEncoder;


object Application extends Controller {
  
  def index = Action {
    Redirect(routes.Application.chess)
  }
  
  def chess = Action {
    Ok(views.html.index(new FENEncoder(GamePosition()).encode))
  }
  
}