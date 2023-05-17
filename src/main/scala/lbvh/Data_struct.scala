package lbvh

import config.Configs._
import chisel3._
import chisel3.util._

class DecoupledIO[T <: Data](gen: T) extends Bundle {
  val ready = Input(Bool())
  val valid = Output(Bool())
  val bits = Output(gen)
}

class Point extends Bundle {
  val x = UInt(DATA_WIDTH.W)
  val y = UInt(DATA_WIDTH.W)
  val z = UInt(DATA_WIDTH.W)
}

class Triangle extends Bundle {
  val tri_id = UInt(ADDR_WIDTH.W)
  val point_0 = new Point
  val point_1 = new Point
  val point_2 = new Point
}

class BoundingBox extends Bundle {
  val maxPoint = new Point
  val minPoint = new Point
}

class Before_sort_primitive extends Bundle {
  val triangle = new Triangle
  val bbox = new BoundingBox
  val morton_code = UInt(Morton_WIDTH.W)
}
