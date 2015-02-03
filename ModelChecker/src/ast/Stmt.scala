package ast

/**
 * Classes used to represent main kinds of statements within the program
 * @author Sofia Boutahar
 * @author David Courtinot
 * @author Xiaowen Ji
 */
class Stmt extends SourceCodeNode 
final case class Type          (name: String)
final case class SwitchStmt    (expr: Expr, cases: CompoundStmt, default: Option[Stmt])           extends Stmt
final case class IfStmt        (condition: Expr, body: CompoundStmt, elseStmt: Option[Stmt])      extends Stmt
final case class SwitchCase    (value: Literal, body: CompoundStmt)                               extends Stmt
final case class AssignmentStmt(variable: String, value: Expr)                                    extends Stmt
final case class DeclStmt      (decls: List[SourceCodeNode])                                      extends Stmt with ForInitializer
final case class CompoundStmt  (val elts: List[SourceCodeNode])                                   extends Stmt 
final case class ReturnStmt    (expr: Expr)                                                       extends Stmt
final case class ContinueStmt  ()                                                                 extends Stmt
final case class BreakStmt     ()                                                                 extends Stmt
final case class LabelStmt     (label: String, body: Stmt)                                        extends Stmt
final case class GotoStmt      (label: String)                                                    extends Stmt
final case class NullStmt      ()                                                                 extends Stmt
