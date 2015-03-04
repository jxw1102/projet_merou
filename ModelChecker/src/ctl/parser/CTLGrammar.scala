package ctl.parser

import scala.util.parsing.combinator._
import ast.ProgramNode
import ast.model.BinaryOp
import ast.model.CallExpr
import ast.model.DeclRefExpr
import ast.model.Expr
import ast.model.CompoundAssignOp
import ast.model._
import ast.model.Literal
import ast.model.OpPosition
import ast.model.UnaryOp
import cfg.CFGMetaVar
import cfg.CFGVal
import cfg.DefinedExpr
import cfg.ExpressionLabelizer
import cfg.ExpressionLabelizer
import cfg.ExpressionLabelizer
import cfg.ExpressionLabelizer
import ctl._
import ctl.CtlExpr

class CTLGrammar extends JavaTokenParsers  {
   type M = CFGMetaVar
   type N =  ProgramNode
   type V = CFGVal
   type E = Expr
   
//    def concat(list:List[CtlExpr[M,N,V]], op:(CtlExpr[M,N,V],CtlExpr[M,N,V]) => CtlExpr[M,N,V]):CtlExpr[M,N,V]  = list match {
//     case List(elem) => elem
//     case elem::tail => elem || concatOR(tail)
//     }
    def concatOR(list:List[CtlExpr[M,N,V]]):CtlExpr[M,N,V]  = list match {
     case List(elem) => elem
     case elem::tail => elem || concatOR(tail)
     }
    def concatAND(list:List[CtlExpr[M,N,V]]):CtlExpr[M,N,V]  = list match {
     case List(elem) => elem
     case elem::tail => elem && concatAND(tail)
     }
      
  lazy val ctl5: Parser[CtlExpr[M,N,V]] = ctl4 ~ rep(("OR") ~> ctl4) ^^ { 
    case left ~ List() => left
    case left ~ list => left || concatOR(list)
//    case left ~ listRight =>  left || concatOR(listRight(0), listRight.tail)  // faire pour tous les elements
    } 
  lazy val ctl4: Parser[CtlExpr[M,N,V]] = ctl3 ~ rep(("AND") ~> ctl3) ^^ { 
    case left ~ List() => left
    case left ~ list => left &&  concatAND(list)
    } 
  lazy val  ctl3:Parser[CtlExpr[M,N,V]] = "A" ~> "[" ~> ctl3 ~ ("U" ~> ctl3 <~ "]")                ^^  { case x ~ y => AU(x,y)}    |
                                                   "E" ~> "[" ~> ctl3 ~ ("U" ~> ctl3 <~ "]")                 ^^ { case x ~ y => EU(x,y) }           |
                                                   "AX"  ~> "(" ~> ctl3 <~ ")"                                           ^^  { case x => AX(x) }                              |
                                                   "EX" ~> ctl3                                                                     ^^  { case x => EX(x)}                               |
                                                   ((("exists" ~>  ident) <~ "(") ~ ctl5 <~ ")") ^^ { case x ~ y => Exists( (CFGMetaVar(x),new NoType[V]) , y) }                           | 
                                                   "NOT" ~> "(" ~> ctl5 <~ ")" ^^ { case x => !x }                                                                             | ctl2
  lazy val ctl2:Parser[CtlExpr[M,N,V]] = ctl1 | assignment ^^ { case x => Predicate(ExpressionLabelizer(DefinedExpr(x))) }
  lazy val ctl1:Parser[CtlExpr[M,N,V]] = "(" ~> ctl5 <~ ")"
  
  
  /***********************************************************************************/
//  val assignment2 :Parser[CtlExpr[M,N,V]] = ass
//  val ass:Parser[CtlExpr[M,N,V]] = opt(ident ~ ("="|"+="|"-="|"*="|"/="|"%="|"&="|"|="|"^="|"<<="|">>=")) ~ exp123 ^^ { 
//    case None ~ right => right
//    case Some(leftIdent ~ leftOp) ~ right => CompoundAssignOp(leftIdent,DeclRefExpr("",leftIdent,"",""),right,leftOp) 
//  }
//  val expr123 :Parser[E] = opt(ident ~ ("="|"+="|"-="|"*="|"/="|"%="|"&="|"|="|"^="|"<<="|">>=")) ~ exp12 ^^ { 
//    case None ~ right => right
//    case Some(leftIdent ~ leftOp) ~ right => Predicate(ExpressionLabelizer(DefinedExpr(CompoundAssignOp(leftIdent,DeclRefExpr("",leftIdent,"",""),right,leftOp) )
//  } 
//  opt(ident ~ ("="|"+="|"-="|"*="|"/="|"%="|"&="|"|="|"^="|"<<="|">>=")) ~ exp12 ^^ { 
//    case None ~ right => right
//    case Some(leftIdent ~ leftOp) ~ right => CompoundAssignOpPattern(DeclRefExpr("",leftIdent,"",""),right,leftOp) 
//  } | exp12 ^^ { case x => x }
  
  val assignment :Parser[E] = opt(ident ~ ("="|"+="|"-="|"*="|"/="|"%="|"&="|"|="|"^="|"<<="|">>=")) ~ exp12 ^^ { 
    case None ~ right => right
    case Some(leftIdent ~ leftOp) ~ right => CompoundAssignOp(leftIdent,DeclRefExpr("",leftIdent,""),right,leftOp) 
  } | exp12 ^^ { case x => x }
  
 
  
  def bopRec(list:List[~[String,E]]):E = list match {
     case List(~(op,elem)) => elem
     case ~(op,elem)::tail =>  BinaryOp("",elem,bopRec(tail),op)
     }
  
  lazy val exp12: Parser[E] = exp11 ~ rep("||" ~ exp11) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  ) //  "exp12:("+left +","+ right+")"
   }
  lazy val exp11: Parser[E] = exp10 ~ rep("&&" ~ exp10) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp10: Parser[E] = exp9 ~ rep("|" ~ exp9) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp9: Parser[E] = exp8 ~ rep("^" ~ exp8) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp8: Parser[E] = exp7 ~ rep("&" ~ exp7) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp7: Parser[E] = exp6 ~ opt(("=="|"!=") ~ exp6) ^^ { 
    case left ~ None => left
    case left ~ Some(op ~ rightExp) => BinaryOp("",left,rightExp,op)
}
  lazy val exp6: Parser[E] = exp5 ~ opt(("<"|">"|"<="|">=") ~ exp5) ^^ { 
    case left ~ None => left
    case left ~ Some(op ~ rightExp) => BinaryOp("",left,rightExp,op)
}
  lazy val exp5: Parser[E] = exp4 ~ opt(("<<"|">>") ~ exp4) ^^ { 
    case left ~ None => left
    case left ~ Some(op ~ rightExp) => BinaryOp("",left,rightExp,op)
    }
  lazy val exp4: Parser[E] = exp3 ~ rep(("+"|"-") ~ exp3) ^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp3: Parser[E] = exp2 ~ rep(("*"|"/"|"%") ~ exp2)^^ { 
    case left ~ List() => left
    case left ~ right => BinaryOp("",left,bopRec(right),right(0)._1  )
    } 
  lazy val exp2: Parser[E] = exp1 ~ ("++"|"--")                                                          ^^ { case x ~ op => UnaryOp("",x, op,OpPosition("postfix")) } |
                                                            opt("!"|"~"|"--"|"++"|"*"|"&"|"-"|"+") ~ exp1 ^^ { case None ~ right => right
                                                                                                                                                                case Some(op) ~ exp => UnaryOp("",exp, op,OpPosition("prefix")) }                                      
  lazy val exp1: Parser[E] =  rep("[A-Z]".r) ^^ { case x => DeclRefExpr("","METAVAR","")} | 
                    funcall |
                    parenth |
                    floatingPointNumber ^^ { case x => if (x.toInt == x.toDouble) Literal("double", x) else Literal("int", x) } |
                    ident   ^^ { case id => DeclRefExpr("",id,"") }
                    
  lazy val funcall: Parser[E] =    (ident <~ "(") ~ opt(exp12 ~ rep(","~>exp12)) <~ ")" ^^ { 
    case  ident ~ None=> CallExpr("",DeclRefExpr("",ident,""),Nil)
    case ident ~ Some(exp ~ list) => CallExpr("",DeclRefExpr("",ident,""),exp::list)}
  
  lazy val parenth: Parser[E] = "(" ~> exp12 <~ ")"  ^^ { case x => x}  

}
