
window.DHTMLGoodies = {}

class window.DHTMLGoodies.ChessFen
  constructor: (props) ->
    @squareSize = 45
    @cssPath = 'css/chess.css'
    @parentRef = document.body
    @imageFolder = 'images/'
    @boardLabels = true
    @flipBoardWhenBlackToMove = true
    @__setInitProps props  if props
    this

  __setInitProps: (props) ->
    @cssPath = props.cssPath  if props.cssPath
    @imageFolder = props.imageFolder  if props.imageFolder
    @squareSize = props.squareSize  if props.squareSize
    @boardLabels = props.boardLabels  if props.boardLabels or props.boardLabels is false
    @flipBoardWhenBlackToMove = props.flipBoardWhenBlackToMove  if props.flipBoardWhenBlackToMove or props.flipBoardWhenBlackToMove is false
    @pieceMovedCallback = props.pieceMovedCallback  if props.pieceMovedCallback

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
    element = $('#' + element)[0]
    console.log('element=' + element)
    element.innerHTML = ''
    boardOuter = document.createElement('div')
    boardOuter.className = 'chess-board'
    boardOuter.style.position = 'relative'
    board = document.createElement('div')
    board.className = 'chess-board-inner'
    board.id = 'chessBoardInnerID'
    that = this
    board.ondrop = (event) ->
      fromSquare = parseInt(event.dataTransfer.getData('text/plain'))
      offset = $('#chessBoardInnerID').offset()
      toSquare = that.__getSquareIndexByBoardPos(event.clientX - offset.left, event.clientY - offset.top)
      that.pieceMovedCallback fromSquare, toSquare
      false

    board.ondragover = ->
      false

    if @boardLabels
      @__addBoardLabels boardOuter
      boardOuter.appendChild board
      board.style.position = 'absolute'
      board.style.top = '0px'
      board.style.right = '0px'
      element.appendChild boardOuter
    else
      board.style.position = 'relative'
      element.appendChild board
    @__loadFen fenString, board

  __addBoardLabels: (boardOuter) ->
    letters = 'ABCDEFGH'
    no_ = 1

    while no_ <= 8
      file = document.createElement('DIV')
      file.style.position = 'absolute'
      file.style.right = ((8 - no_) * @squareSize) + 'px'
      file.style.bottom = '0px'
      file.innerHTML = letters.substr((no_ - 1), 1)
      file.style.textAlign = 'center'
      file.style.width = @squareSize + 'px'
      boardOuter.appendChild file
      file.className = 'chess-board-label'
      rank = document.createElement('DIV')
      rank.style.position = 'absolute'
      rank.style.left = '0px'
      rank.style.top = ((8 - no_) * @squareSize) + 'px'
      rank.innerHTML = no_
      rank.style.height = @squareSize + 'px'
      rank.style.lineHeight = @squareSize + 'px'
      boardOuter.appendChild rank
      rank.className = 'chess-board-label'
      if @whoToMove is 'b' and @flipBoardWhenBlackToMove
        rank.innerHTML = 9 - no_
        file.innerHTML = letters.substr((8 - no_), 1)
      no_++

  __loadFen: (fenString, boardEl) ->
    @__createSquares(boardEl)
    items = fenString.split(/\s/g)
    pieces = items[0]
    currentCol = 0
    color = 'w'
    that = this
    onDragStart = (event) ->
      pieceDiv = event.currentTarget.parentElement
      fromSquare = that.__getSquareIndexByBoardPos(pieceDiv.offsetLeft, pieceDiv.offsetTop)
      event.dataTransfer.setData 'text/plain', fromSquare

    no_ = 0

    while no_ < pieces.length
      square = $('#square' + currentCol)[0]
      square.empty;
      character = pieces.substr(no_, 1)
      if character.match(/[A-Z]/i)
        piece = @__createPiece(character, onDragStart)
        square.appendChild piece
        currentCol++
      else currentCol += character / 1  if character.match(/[0-8]/)
      no_++

  __createSquares: (boardEl) ->
    for squareIndex in [0..63]
      boardEl.appendChild(@__createSquare(squareIndex))
    
  __createSquare: (squareIndex) ->
    row = Math.floor(squareIndex / 8)
    column = Math.floor(squareIndex % 8)
    square = document.createElement('div')
    boardPos = @__getBoardPosByCol(squareIndex)
    square.style.position = 'absolute'
    square.style.left = boardPos.x + 'px'
    square.style.top = boardPos.y + 'px'
    square.className = 'chess-square-' + (if row % 2 == column % 2 then 'black' else 'white')
    square.id = 'square' + squareIndex 
    square

  __createPiece: (character, onDragStart) ->
    piece = document.createElement('p')
    piece.innerHTML = @__getUnicodeForPiece(character)
    piece.draggable = true
    piece.ondragstart = onDragStart
    piece.className = 'chess-piece'
    piece    

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
    if @whoToMove is 'b' and @flipBoardWhenBlackToMove
      col = 7 - col
      rank = 7 - rank
    retArray.x = col * @squareSize
    retArray.y = rank * @squareSize
    retArray

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
      lookup[piece]
  )()
