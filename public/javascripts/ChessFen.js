/*******************************************************************************
 * Chess Fen Viewer Copyright (C) 2007 DTHMLGoodies.com, Alf Magne Kalleland
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * Dhtmlgoodies.com., hereby disclaims all copyright interest in this script
 * written by Alf Magne Kalleland.
 * 
 * Alf Magne Kalleland, 2007 Owner of DHTMLgoodies.com
 * 
 * 
 ******************************************************************************/

var document = function () {
	"use strict";
	return this;
}();

var DHTMLGoodies = {};
if (!String.trim) {
	String.prototype.trim = function () {
		return this.replace(/^\s+|\s+$/, '');
	};
}
/* The widget */
DHTMLGoodies.ChessFen = function (props) {

	this.pieceType = 'cases';
	this.squareSize = 45;
	this.cssPath = 'css/chess.css';
	this.parentRef = document.body;
	this.imageFolder = 'images/';
	this.boardLabels = true;
	this.flipBoardWhenBlackToMove = true;

	if (props) {
		this.__setInitProps(props);
	}
	this.init();
};

DHTMLGoodies.ChessFen.prototype = {
	__setInitProps : function (props) {
		if (props.cssPath) {
			this.cssPath = props.cssPath;
		}
		if (props.imageFolder) {
			this.imageFolder = props.imageFolder;
		}
		if (props.squareSize) {
			this.squareSize = props.squareSize;
		}
		if (props.boardLabels || props.boardLabels === false) {
			this.boardLabels = props.boardLabels;
		}
		if (props.flipBoardWhenBlackToMove || props.flipBoardWhenBlackToMove === false) {
			this.flipBoardWhenBlackToMove = props.flipBoardWhenBlackToMove;
		}
		if (props.pieceType) {
			this.pieceType = props.pieceType;
		}
		if (props.pieceMovedCallback) {
			this.pieceMovedCallback = props.pieceMovedCallback;
		}
	},
	setSquareSize : function (squareSize) {
		this.squareSize = squareSize;
	},
	setPieceType : function (pieceType) {
		this.pieceType = pieceType;
	},
	init : function () {
		this.__loadCss(this.cssPath);
	},
	setFlipBoardWhenBlackToMove : function (flipBoardWhenBlackToMove) {
		this.flipBoardWhenBlackToMove = flipBoardWhenBlackToMove;
	},
	getWhoToMove : function () {
		return this.whoToMove;
	},
	setBoardLabels : function (boardLabels) {
		this.boardLabels = boardLabels;
	},
	__setWhoToMove : function (fenString) {
		var items = fenString.split(/\s/g);
		this.whoToMove = items[1].trim();
	},
	loadFen : function (fenString, element) {
		this.__setWhoToMove(fenString);

		element = this.__getEl(element);
		element.innerHTML = '';
		var boardOuter = document.createElement('DIV');
		boardOuter.className = 'ChessBoard' + this.squareSize;
		boardOuter.style.position = 'relative';

		var board = document.createElement('DIV');
		board.className = 'ChessBoardInner' + this.squareSize;
		board.id = 'chessBoardInnerID';
		var that = this;
		board.ondrop = function (event) {
			var fromSquare = parseInt(event.dataTransfer.getData('text/plain'));
			var offset = $("#chessBoardInnerID").offset();
			var toSquare = that.__getSquareIndexByBoardPos(event.clientX - offset.left, event.clientY - offset.top);
			that.pieceMovedCallback(fromSquare, toSquare);
			return false;
		};
		board.ondragover = function () {
			return false;
		};

		if (this.boardLabels) {
			this.__addBoardLabels(boardOuter);
			boardOuter.appendChild(board);
			board.style.position = 'absolute';
			board.style.top = '0px';
			board.style.right = '0px';
			element.appendChild(boardOuter);
		} else {
			board.style.position = 'relative';
			element.appendChild(board);
		}
		this.__loadFen(fenString, board);

	},
	__addBoardLabels : function (boardOuter) {
		var letters = 'ABCDEFGH';
		for (var no = 1; no <= 8; no++) {
			var file = document.createElement('DIV');
			file.style.position = 'absolute';
			file.style.right = ((8 - no) * this.squareSize) + 'px';
			file.style.bottom = '0px';
			file.innerHTML = letters.substr((no - 1), 1);
			file.style.textAlign = 'center';
			file.style.width = this.squareSize + 'px';
			boardOuter.appendChild(file);
			file.className = 'ChessBoardLabel ChessBoardLabel' + this.squareSize;

			var rank = document.createElement('DIV');
			rank.style.position = 'absolute';
			rank.style.left = '0px';
			rank.style.top = ((8 - no) * this.squareSize) + 'px';
			
			rank.innerHTML = no;
			rank.style.height = this.squareSize + 'px';
			rank.style.lineHeight = this.squareSize + 'px';
			boardOuter.appendChild(rank);
			rank.className = 'ChessBoardLabel ChessBoardLabel' + this.squareSize;

			if (this.whoToMove === 'b' && this.flipBoardWhenBlackToMove) {
				rank.innerHTML = 9 - no;
				file.innerHTML = letters.substr((8 - no), 1);
			}
		}

	},
	// Load Forsyth-Edwards Notation (FEN)
	__loadFen : function (fenString, boardEl) {
		var items = fenString.split(/\s/g);
		var pieces = items[0];

		var currentCol = 0;
		var color = 'w';
		var that = this;
		var onDragStart = function (event) {
			var pieceDiv = event.currentTarget.parentElement;
			var fromSquare = that.__getSquareIndexByBoardPos(
					pieceDiv.offsetLeft, pieceDiv.offsetTop);
			event.dataTransfer.setData('text/plain', fromSquare);
		};		
		for (var no = 0; no < pieces.length; no++) {
			var character = pieces.substr(no, 1);

			if (character.match(/[A-Z]/i)) {
				var boardPos = this.__getBoardPosByCol(currentCol);
				var piece = document.createElement('DIV');
				var span = document.createElement('p');
				span.innerHTML = this.__getUnicodeForPiece(character);
				span.draggable=true;
				span.ondragstart = onDragStart;
				span.className = 'Span45';

				piece.style.position = 'absolute';
				piece.style.left = boardPos.x + 'px';
				piece.style.top = boardPos.y + 'px';

				piece.className = 'ChessPiece' + this.squareSize;
				piece.appendChild(span);
				boardEl.appendChild(piece);
				currentCol++;
			}
			else if (character.match(/[0-8]/)) {
				currentCol += character / 1;
			}
		}

	},
	__getSquareIndexByBoardPos : function (x, y) {
		var row = 7 - Math.floor(y / this.squareSize);
		var column = Math.floor(x / this.squareSize);
		var square = row * 8 + column;
		return square;
	},
	/* Starting from the top */
	/* 1-64 */
	__getBoardPosByCol : function (col) {
		var rank = 0;
		while (col >= 8) {
			rank++;
			col -= 8;
		}
		var retArray = {};

		if (this.whoToMove === 'b' && this.flipBoardWhenBlackToMove) {
			col = 7 - col;
			rank = 7 - rank;
		}

		retArray.x = col * this.squareSize;
		retArray.y = rank * this.squareSize;
		return retArray;

	},
	__loadCss : function (cssFile) {
		var lt = document.createElement('LINK');
		lt.href = cssFile + '?rand=' + Math.random();
		lt.rel = 'stylesheet';
		lt.media = 'screen';
		lt.type = 'text/css';
		document.getElementsByTagName('HEAD')[0].appendChild(lt);
	},
	__getEl : function (elRef) {
		if (typeof elRef === 'string') {
			if (document.getElementById(elRef)) {
				return document.getElementById(elRef);
			}
			if (document.forms[elRef]) {
				return document.forms[elRef];
		    }
			if (document[elRef]) {
				return document[elRef];
			}
			if (window[elRef]) {
				return window[elRef];
			}
		}
		return elRef; // Return original ref.
	},
	__getUnicodeForPiece : (function () {
		var lookup = {
				P: '\u2659', N: '\u2658', B: '\u2657', R: '\u2656',	Q: '\u2655', K: '\u2654',
				p: '\u265F', n: '\u265E', b: '\u265D', r: '\u265C', q: '\u265B', k: '\u265A'
		};
		return function (piece) {
			console.log(piece)
			return lookup[piece];
		};
	})()
};