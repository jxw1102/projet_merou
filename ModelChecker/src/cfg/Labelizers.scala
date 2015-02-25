package cfg

import scala.reflect.runtime.universe

import ast.Expression
import ast.For
import ast.If
import ast.ProgramNode
import ast.Statement
import ast.While
import ast.model._
import ctl.BindingsEnv
import ctl.Bottom
import ctl.ConvertEnv
import ctl.Environment
import ctl.Labelizer
import ctl.MetaVariable
import ctl.TypeOf
import ctl.Value

/**
 * @author Zohour Abouakil
 * @author Xiaowen Ji
 * @author David Courtinot
 */

// TODO
// unused variables : Decl(X) and AX(Unused(X))
// hidden definitions : Decl(X) and EF(Decl(Y) and SameDef(X,Y))

/* 
 * /////////////////////// Labelizers ///////////////////////
 */
case class FindExprLabelizer(pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
    private def foldRec(exprs: List[Expr])        = exprs.foldLeft[Option[Env]](None)((res,e) => if (res.isDefined) res else recMatch(e))
    private def recMatch(expr: Expr): Option[Env] = pattern.matches(expr).orElse(foldRec(expr.getSubExprs))
	override def test(t: ProgramNode)             = foldRec(ConvertNodes.getExprs(t))
}

class IfLabelizer(val pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
    override def test(t: ProgramNode) = t match {
        case If(expr,_,_) => pattern.matches(expr) 
        case _            => None 
    }
}

class ForLabelizer(val pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal]  {
    override def test(t: ProgramNode) = t match {
        case For(Some(expr),_,_) => pattern.matches(expr) 
        case _                   => None 
    }
}

class WhileLabelizer(val pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal]  {
    override def test(t: ProgramNode) = t match {
        case While(expr,_,_) => pattern.matches(expr)
        case _               => None 
    }
}

class ExpressionLabelizer(val pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
	override def test(t: ProgramNode) = t match {
		case Expression(e,_,_) => pattern.matches(e)
		case _                 => None 
	}
}

case class ArithmeticPointerLabelizer() extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
    override def test(t: ProgramNode) = 
    	ConvertNodes.getExprs(t).find {
    	    case x: CompoundAssignOp if x.isPointer => true
    	    case x: BinaryOp         if x.isPointer => true
    	    case x: UnaryOp          if x.isPointer => true
    	    case _                                  => false
    	} match {
    	    case Some(_) => Some(new BindingsEnv)
    	    case None    => None
    	}
}
        
class StatementLabelizer(val pattern: DeclPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
    override def test(t: ProgramNode): Option[Environment[CFGMetaVar, CFGVal]] = t match {
        case Statement(stmt: Decl,_,_) => pattern.matches(stmt)
        case _ => None     
    }
}

class UseLabelizer(val pattern: ExprPattern) extends Labelizer[CFGMetaVar, ProgramNode, CFGVal] {
    override def test(t: ProgramNode) = t match {
        case Expression(e,_,_)                      => pattern.matches(e)
        case While     (e,_,_)                      => pattern.matches(e)
        case If        (e,_,_)                      => pattern.matches(e) 
        case For       (Some(e),_,_)                => pattern.matches(e) 
        case Statement (VarDecl(_, _, Some(e)),_,_) => pattern.matches(e)
        case _                                      => None 
    }
} 

case class DeadIfLabelizer() extends Labelizer[CFGMetaVar,ProgramNode,CFGVal] {
    override def test(t: ProgramNode) = t match {
        case If(Literal(_,_),_,_) => Some(new BindingsEnv) 
        case _                    => None
    }
}

case class ReturnLabelizer(pattern: ExprPattern)  extends Labelizer[CFGMetaVar,ProgramNode,CFGVal] {
    override def test(t: ProgramNode) = t match {
        case Statement(ReturnStmt(_,expr),_,_) => Some(new BindingsEnv ++ (CFGMetaVar("X") -> CFGExpr(expr)))
        case _                                 => None
    }
}

case class VarDeclLabelizer(pattern: VarDeclPattern) extends Labelizer[CFGMetaVar,ProgramNode,CFGVal] {
    // TODO : on renvoie ici des CFGDecl dans l'environnement
    override def test(t: ProgramNode) = ???
}

case class VarDefLabelizer(pattern: VarDeclPattern) extends Labelizer[CFGMetaVar,ProgramNode,CFGVal] {
    // TODO : on renvoie ici des CFGDef dans l'environnement. 
    // NB : il y a sans doute du code en commun avec VarDeclLabelizer, factoriser n'est pas une mauvaise idée !
    override def test(t: ProgramNode) = ???
}


//case class DeclLabelizer()  extends Labelizer[CFGMetaVar,ProgramNode,CFGVal] {
//    override def test(t: ProgramNode): Option[Environment[CFGMetaVar, CFGVal]] = t match {
//        case Decl() => 
//        case _                                 => None
//    }
//}
