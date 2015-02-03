package ast

/**
 * Classes used to represent different kinds of loops within the program
 * @author Sofia Boutahar
 * @author David Courtinot
 * @author Xiaowen Ji
 */
trait ForInitializer
final case class EnhancedForStmt(iterable: String, loopVar: Decl, body: CompoundStmt)                                        extends Stmt
final case class ForStmt        (init: Option[ForInitializer], cond: Option[Expr], update: Option[Expr], body: CompoundStmt) extends Stmt
final case class WhileStmt      (condition: Expr, body: CompoundStmt)                                                        extends Stmt
final case class DoWhileStmt    (condition: Expr, body: CompoundStmt)                                                        extends Stmt