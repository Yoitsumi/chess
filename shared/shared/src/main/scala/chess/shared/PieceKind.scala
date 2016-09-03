package chess.shared

/**
 * Created by Kamil on 28.08.2016.
 */
sealed trait PieceKind

case object Pawn extends PieceKind
case object Rook extends PieceKind
case object Knight extends PieceKind
case object Bishop extends PieceKind
case object King extends PieceKind
case object Queen extends PieceKind
