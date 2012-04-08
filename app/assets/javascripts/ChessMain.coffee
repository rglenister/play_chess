
window.ChessMain = {}

class window.ChessMain.Chess
  constructor: (webSocketURL) ->
    @pieceMoved = (move) =>
      @sendMessage { action: 'makeMove', args: move }    
    props = { pieceMovedCallback: @pieceMoved }
    @chessObj = new window.DHTMLGoodies.ChessFen(props)
    boardDiv = $("#chessBoard1")[0]
    @chessObj.init boardDiv
    @addButtonHandlers()
    WS = (if window["MozWebSocket"] then MozWebSocket else WebSocket)
    @chessSocket = new WS(webSocketURL)
   
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
    
  seekTo: (index) ->
    @sendMessage { action: 'setCurrentPosition', args: { positionIndex: index } }

  confirmNewGame: =>
    newGame = =>
      @sendMessage { action: 'newGame' }

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
  
  addButtonHandlers: ->
    $('#newGameButton')[0].onclick = @confirmNewGame
    $('#firstPositionButton')[0].onclick = => @seekTo 0
    $('#previousPositionButton')[0].onclick = => @seekTo @game.positionIndex - 1
    $('#nextPositionButton')[0].onclick = => @seekTo @game.positionIndex + 1
    $('#lastPositionButton')[0].onclick = => @seekTo @game.moveHistory.length
    
  init: ->    
    @chessSocket.onmessage = @receiveEvent  
    timeout = window.setTimeout(=>
      @pieceMoved
        from: 0
        to: 0
    , 500)
  
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
      moveEl.setAttribute('class', 'moveText') 
      moveEl.appendChild(document.createTextNode(move))
      moveEl.id = 'move' + index
      if index == @game.positionIndex
        moveEl.setAttribute('class', 'moveTextSelected')       
      moveEl.onmousedown = @onMoveClicked.bind(this)
      rootEl.appendChild(moveEl)
      moveHistoryEl.appendChild(rootEl)

  onMoveClicked: (event) ->
    moveNumber = event.currentTarget.id.substr(4)
    @seekTo parseInt(moveNumber)
    
    