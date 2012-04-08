
window.ChessMain = {}

class window.ChessMain.Chess
  constructor: (webSocketURL) ->
    props = { pieceMovedCallback: @pieceMoved }
    @chessObj = new window.DHTMLGoodies.ChessFen(props)
    boardDiv = $("#chessBoard1")[0]
    @chessObj.init boardDiv
    @addButtonHandlers()
    WS = (if window["MozWebSocket"] then MozWebSocket else WebSocket)
    @chessSocket = new WS(webSocketURL)
   
  init: ->    
    @chessSocket.onmessage = @receiveEvent  
    timeout = window.setTimeout(=>
      @sendGetGameMessage()
    , 500)
  
  pieceMoved: (move) =>
    if @game?
      if @game.positionIndex == @game.moveHistory.length 
        @sendPieceMovedMessage move
      else
        @confirmTruncateGame => @sendPieceMovedMessage move
        
  sendPieceMovedMessage: (move) ->
    @sendMessage { action: 'makeMove', args: move }
        
  sendGetGameMessage: =>
    @sendMessage { action: 'getGame' }

  sendNewGameMessage: =>
    @sendMessage { action: 'newGame' }

  sendSeekToMessage: (index) ->
    @sendMessage { action: 'setCurrentPosition', args: { positionIndex: index } }

  sendMessage: (message) ->
    console.log "sending message", message
    json = JSON.stringify(message)
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
      @game = data.game
      @chessObj.loadGame data.game, @boardDiv
      $('#whitePlayerName').text(data.game.players.white)
      $('#blackPlayerName').text(data.game.players.black)
      @generateMoveHistory(data.game.moveHistory)
      
    $("#members").html ""
    $(data.members).each ->
      $("#members").append "<li>" + this + "</li>"

  confirmNewGame: (newGame) ->
    $("#dialog-confirm-new-game").dialog({
      resizable: false,
      height:160,
      modal: true,
      buttons: {
        "Yes": ->
          $(this).dialog("close")
          newGame()
        Cancel: ->
          $(this).dialog("close")
      }
    })
  
  confirmTruncateGame: (makeMove) ->
    $("#dialog-confirm-truncate-game").dialog({
      resizable: false,
      height:160,
      modal: true,
      buttons: {
        "Yes": ->
          $(this).dialog("close")
          makeMove()
        Cancel: ->
          $(this).dialog("close")
      }
    })
  
  addButtonHandlers: ->
    $('#newGameButton')[0].onclick = => @confirmNewGame @sendNewGameMessage
    $('#firstPositionButton')[0].onclick = => @sendSeekToMessage 0
    $('#previousPositionButton')[0].onclick = => @sendSeekToMessage @game.positionIndex - 1
    $('#nextPositionButton')[0].onclick = => @sendSeekToMessage @game.positionIndex + 1
    $('#lastPositionButton')[0].onclick = => @sendSeekToMessage @game.moveHistory.length
    
  generateMoveHistory: (moveHistory) ->
    $('#moveHistory').html('')
    moveHistoryEl = $('#moveHistory')[0]
    for move, index in moveHistory
      rootEl = document.createElement('span')
      if index % 2 == 0
        moveNumberEl = document.createElement('span')
        moveNumberEl.setAttribute('class', 'moveNumber')
        moveNumberEl.appendChild(document.createTextNode('' + (index / 2 + 1) + '.'))
        rootEl.appendChild(moveNumberEl)
      moveEl = document.createElement('span')
      if index == @game.positionIndex - 1
        moveEl.setAttribute('class', 'moveTextSelected')  
      else
        moveEl.setAttribute('class', 'moveText') 
      moveEl.appendChild(document.createTextNode(move))
      moveEl.id = 'move' + index
     
      moveEl.onmousedown = @onMoveClicked.bind(this)
      rootEl.appendChild(moveEl)
      moveHistoryEl.appendChild(rootEl)

  onMoveClicked: (event) ->
    moveNumber = event.currentTarget.id.substr(4)
    @sendSeekToMessage parseInt(moveNumber) + 1
    
    