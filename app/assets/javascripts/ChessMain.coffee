
window.ChessMain = {}

class window.ChessMain.Chess
  constructor: (webSocketURL) ->
    @pieceMoved = (move) =>
      @sendMessage move    
    props = { pieceMovedCallback: @pieceMoved }
    @chessObj = new window.DHTMLGoodies.ChessFen(props)
    boardDiv = $("#" + "chessBoard1")[0]
    @chessObj.init boardDiv
    WS = (if window["MozWebSocket"] then MozWebSocket else WebSocket)
    @chessSocket = new WS(webSocketURL)
    
  sendMessage: (move) ->
    console.log "sending message", move
    json = JSON.stringify(move)
    console.log "json=", json
    @chessSocket.send json
    
  receiveEvent: (event) =>
    data = JSON.parse(event.data)
    if data.error
      chessSocket.close()
      $("#onError span").text data.error
      $("#onError").show()
      return
    else
      $("#onChat").show()
    if data.kind is "game"
      console.log "received message -", data.game
      @chessObj.loadGame data.game, @boardDiv
    $("#members").html ""
    $(data.members).each ->
      $("#members").append "<li>" + this + "</li>"

  init: ->    
    @chessSocket.onmessage = @receiveEvent  
    timeout = window.setTimeout(=>
      @pieceMoved
        from: 0
        to: 0
    , 500)
  