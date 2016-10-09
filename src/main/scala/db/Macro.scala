package db

import scala.reflect.macros.blackbox

object Macro {

  def hello: Unit = macro helloImpl

  def helloImpl(c: blackbox.Context): c.Expr[Unit] = {
    import c.universe._
    c.Expr(q"""println("hello!")""")
  }
}
