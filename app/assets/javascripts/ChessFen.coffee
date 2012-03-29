
window.DHTMLGoodies = {}

class window.DHTMLGoodies.ChessFen
  constructor: (props) ->
    @squareSize = 45
    @cssPath = 'css/chess.css'
    @parentRef = document.body
    @boardLabels = true
    @__setInitProps props  if props

  __setInitProps: (props) ->
    @cssPath = props.cssPath  if props.cssPath
    @squareSize = props.squareSize  if props.squareSize
    @boardLabels = props.boardLabels  if props.boardLabels or props.boardLabels is false
    @pieceMovedCallback = props.pieceMovedCallback  if props.pieceMovedCallback

  setBoardLabels: (boardLabels) ->
    @boardLabels = boardLabels

  __occupied: (squareIndex) ->
    @__squareToPieceMap[squareIndex]?

  __findTeachingSquares: (fromSquare) ->
    [20, 28, 36, 44, 52]
    
  __onMouseDown: (event) ->
    squareDiv = event.currentTarget.parentElement
    fromSquare = squareDiv.id
    pieceChar = @__squareToPieceMap[fromSquare]
    @__teachingSquares = @__findTeachingSquares(fromSquare)
    for i in @__teachingSquares
      square = $('#' + i)[0]
      if !@__occupied(i)
        square.appendChild(@__createPiece(pieceChar, 'chess-piece-teaching'))
      else
        square.children[0].className = 'chess-piece-threatened'

  __onMouseUp: (event) ->
    for i in @__teachingSquares
      squareDiv = $('#' + i)[0]
      if !@__occupied(i)
        squareDiv.innerHTML = ''
      else
        squareDiv.children[0].className = 'chess-piece'
      delete @__teachingSquares

  __onDragStart: (event) ->
    pieceDiv = event.currentTarget.parentElement
    fromSquare = pieceDiv.id
    event.dataTransfer.setData 'text/plain', fromSquare

  __onDrop: (event) ->
    fromSquare = event.dataTransfer.getData('text/plain')
    toSquare = event.target.id
    if toSquare == "" then toSquare = event.target.parentNode.id
    @pieceMovedCallback parseInt(fromSquare), parseInt(toSquare)
    false

  init: (element) ->
    element.innerHTML = ''
    boardOuter = document.createElement('div')
    boardOuter.className = 'chess-board'
    boardOuter.style.position = 'relative'
    board = document.createElement('div')
    board.className = 'chess-board-inner'
    board.ondrop = @__onDrop.bind(this)

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
    @__createSquares(board)

  loadFen: (fenString, element) ->
    boardEl = $('.chess-board-inner')[0]
    $(boardEl.children).empty()
    @__squareToPieceMap = @__decodeFen fenString

    for index, pieceChar of @__squareToPieceMap
      square = $('#' + index)[0]
      square.empty;
      piece = @__createPiece(pieceChar, 'chess-piece')
      piece.ondragstart = @__onDragStart.bind(this)
      piece.ondragenter = (event) -> return false
      piece.onmousedown = @__onMouseDown.bind(this)
      piece.onmouseup = @__onMouseUp.bind(this)
      square.appendChild piece

  __decodeFen: (fen) ->
    encodedPieceArray = fen.split(/\s/g)[0].split('/').reverse().join('').split('')
    pieceArray = _.reduce encodedPieceArray, ((sum, s) -> sum.concat(if isNaN(s) then [s] else Array(Number(s)))), []
    squareToPieceMap = {}
    for piece, i in pieceArray
      squareToPieceMap[i] = piece if pieceArray[i]
    squareToPieceMap

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
    square.id = squareIndex 
    square

  __createPiece: (character, clazz) ->
    piece = document.createElement('p')
    piece.innerHTML = @__getUnicodeForPiece(character)
    piece.draggable = true
    piece.className = clazz
    piece    

  __getBoardPosByCol: (squareIndex) ->
    row = 7 - Math.floor(squareIndex / 8)
    column = Math.floor(squareIndex % 8)
    retArray = {}
    retArray.y = row * @squareSize
    retArray.x = column * @squareSize
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
      no_++

  