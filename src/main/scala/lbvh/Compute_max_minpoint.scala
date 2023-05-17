package lbvh

import chisel3._
import hardfloat._
import config.Configs._

class Compute_max_minpoint extends Module { // 组合逻辑
  val io = IO(new Bundle {
    val input_vec = Input(new Point)
    val out_vec_min = Output(UInt(DATA_WIDTH.W))
    val out_vec_max = Output(UInt(DATA_WIDTH.W))
  })

  val F_cmp_le_x1 = Module(new Float_CMP_le)
  val F_cmp_le_xmin = Module(new Float_CMP_le)
  val F_cmp_le_xmax = Module(new Float_CMP_le)

  F_cmp_le_x1.io.a := io.input_vec.x
  F_cmp_le_x1.io.b := io.input_vec.y

  when(F_cmp_le_x1.io.out_bool) {
    F_cmp_le_xmin.io.a := io.input_vec.x
    F_cmp_le_xmax.io.a := io.input_vec.y
  }.otherwise {
    F_cmp_le_xmin.io.a := io.input_vec.y
    F_cmp_le_xmax.io.a := io.input_vec.x
  }
  F_cmp_le_xmin.io.b := io.input_vec.z
  F_cmp_le_xmax.io.b := io.input_vec.z

  when(F_cmp_le_xmin.io.out_bool) {
    io.out_vec_min := F_cmp_le_xmin.io.a
  }.otherwise {
    io.out_vec_min := io.input_vec.z
  }

  when(F_cmp_le_xmax.io.out_bool) {
    io.out_vec_max := io.input_vec.z
  }.otherwise {
    io.out_vec_max := F_cmp_le_xmax.io.a
  }

}
