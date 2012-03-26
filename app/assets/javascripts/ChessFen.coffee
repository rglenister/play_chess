
window.DHTMLGoodies = {}
unless String.trim
  String::trim = ->
    @replace /^\s+|\s+$/, ""

class window.DHTMLGoodies.ChessFen
  constructor: (props) ->
    @pieceType = "cases"
    @squareSize = 45
    @cssPath = "css/chess.css"
    @parentRef = document.body
    @imageFolder = "images/"
    @boardLabels = true
    @flipBoardWhenBlackToMove = true
    console.log('@__setInitProps', @__setInitProps)
    @__setInitProps props  if props
    @init()
    return this

  __setInitProps: (props) ->
    @cssPath = props.cssPath  if props.cssPath
    @imageFolder = props.imageFolder  if props.imageFolder
    @squareSize = props.squareSize  if props.squareSize
    @boardLabels = props.boardLabels  if props.boardLabels or props.boardLabels is false
    @flipBoardWhenBlackToMove = props.flipBoardWhenBlackToMove  if props.flipBoardWhenBlackToMove or props.flipBoardWhenBlackToMove is false
    @pieceType = props.pieceType  if props.pieceType
    @pieceMovedCallback = props.pieceMovedCallback  if props.pieceMovedCallback

  setSquareSize: (squareSize) ->
    @squareSize = squareSize

  setPieceType: (pieceType) ->
    @pieceType = pieceType

  init: ->
    @__loadCss @cssPath

  setFlipBoardWhenBlackToMove: (flipBoardWhenBlackToMove) ->
    @flipBoardWhenBlackToMove = flipBoardWhenBlackToMove

  getWhoToMove: ->
    @whoToMove

  setBoardLabels: (boardLabels) ->
    @boardLabels = boardLabels

  __setWhoToMove: (fenString) ->
    items = fenString.split(/\s/g)
    @whoToMove = items[1].trim()

  loadFen: (fenString, element) ->
    @__setWhoToMove fenString
    element = @__getEl(element)
    element.innerHTML = ""
    boardOuter = document.createElement("DIV")
    boardOuter.className = "ChessBoard" + @squareSize
    boardOuter.style.position = "relative"
    board = document.createElement("DIV")
    board.className = "ChessBoardInner" + @squareSize
    board.id = "chessBoardInnerID"
    that = this
    board.ondrop = (event) ->
      fromSquare = parseInt(event.dataTransfer.getData("text/plain"))
      offset = $("#chessBoardInnerID").offset()
      toSquare = that.__getSquareIndexByBoardPos(event.clientX - offset.left, event.clientY - offset.top)
      that.pieceMovedCallback fromSquare, toSquare
      false

    board.ondragover = ->
      false

    if @boardLabels
      @__addBoardLabels boardOuter
      boardOuter.appendChild board
      board.style.position = "absolute"
      board.style.top = "0px"
      board.style.right = "0px"
      element.appendChild boardOuter
    else
      board.style.position = "relative"
      element.appendChild board
    @__loadFen fenString, board

  __addBoardLabels: (boardOuter) ->
    letters = "ABCDEFGH"
    no_ = 1

    while no_ <= 8
      file = document.createElement("DIV")
      file.style.position = "absolute"
      file.style.right = ((8 - no_) * @squareSize) + "px"
      file.style.bottom = "0px"
      file.innerHTML = letters.substr((no_ - 1), 1)
      file.style.textAlign = "center"
      file.style.width = @squareSize + "px"
      boardOuter.appendChild file
      file.className = "ChessBoardLabel ChessBoardLabel" + @squareSize
      rank = document.createElement("DIV")
      rank.style.position = "absolute"
      rank.style.left = "0px"
      rank.style.top = ((8 - no_) * @squareSize) + "px"
      rank.innerHTML = no_
      rank.style.height = @squareSize + "px"
      rank.style.lineHeight = @squareSize + "px"
      boardOuter.appendChild rank
      rank.className = "ChessBoardLabel ChessBoardLabel" + @squareSize
      if @whoToMove is "b" and @flipBoardWhenBlackToMove
        rank.innerHTML = 9 - no_
        file.innerHTML = letters.substr((8 - no_), 1)
      no_++

  __loadFen: (fenString, boardEl) ->
    items = fenString.split(/\s/g)
    pieces = items[0]
    currentCol = 0
    color = "w"
    that = this
    onDragStart = (event) ->
      pieceDiv = event.currentTarget.parentElement
      fromSquare = that.__getSquareIndexByBoardPos(pieceDiv.offsetLeft, pieceDiv.offsetTop)
      event.dataTransfer.setData "text/plain", fromSquare

    no_ = 0

    while no_ < pieces.length
      character = pieces.substr(no_, 1)
      if character.match(/[A-Z]/i)
        boardPos = @__getBoardPosByCol(currentCol)
        piece = document.createElement("DIV")
        span = document.createElement("p")
        span.innerHTML = @__getUnicodeForPiece(character)
        span.draggable = true
        span.ondragstart = onDragStart
        span.className = "Span45"
        piece.style.position = "absolute"
        piece.style.left = boardPos.x + "px"
        piece.style.top = boardPos.y + "px"
        piece.className = "ChessPiece" + @squareSize
        piece.appendChild span
        boardEl.appendChild piece
        currentCol++
      else currentCol += character / 1  if character.match(/[0-8]/)
      no_++

  __getSquareIndexByBoardPos: (x, y) ->
    row = 7 - Math.floor(y / @squareSize)
    column = Math.floor(x / @squareSize)
    square = row * 8 + column
    square

  __getBoardPosByCol: (col) ->
    rank = 0
    while col >= 8
      rank++
      col -= 8
    retArray = {}
    if @whoToMove is "b" and @flipBoardWhenBlackToMove
      col = 7 - col
      rank = 7 - rank
    retArray.x = col * @squareSize
    retArray.y = rank * @squareSize
    retArray

  __loadCss: (cssFile) ->
    lt = document.createElement("LINK")
    lt.href = cssFile + "?rand=" + Math.random()
    lt.rel = "stylesheet"
    lt.media = "screen"
    lt.type = "text/css"
    document.getElementsByTagName("HEAD")[0].appendChild lt

  __getUnicodeForPiece: (->
    lookup =
      P: '\u2659'
      N: '\u2658'
      B: '\u2657'
      R: '\u2656'
      Q: '\u2655'
      K: '\u2654'
      p: '\u265F'
      n: '\u265E'
      b: '\u265D'
      r: '\u265C'
      q: '\u265B'
      k: '\u265A'


    (piece) ->
      console.log piece
      lookup[piece]
  )()
  __getEl: (elRef) ->
    if typeof elRef is "string"
      return document.getElementById(elRef)  if document.getElementById(elRef)
      return document.forms[elRef]  if document.forms[elRef]
      return document[elRef]  if document[elRef]
      return window[elRef]  if window[elRef]
    elRef
    