package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._
import fifo._
import firrtl.flattenType

class ResultIO extends Bundle {
  val input2 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }
  val input3 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }

  val input1 = new Bundle {
    val node = Input(new Node)
    val valid = Input(Bool())
  }

  val output1 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }

  val output2 = new Bundle {
    val node = Output(new Node)
    val valid = Output(Bool())
  }
}

class Result extends Module {
  val io = IO(new ResultIO)
  val mem = SyncReadMem(Node_count, new Node)
  val out_validReg = Reg(Vec(2, Bool()))
  val out_validReg2 = Reg(Vec(2, Bool()))

  val temp_node_idReg = Reg(Vec(3, UInt(ADDR_WIDTH.W)))

  out_validReg2 := out_validReg

  when(io.input2.valid) {
    mem.write(io.input2.node.node_id, io.input2.node)
    temp_node_idReg(0) := io.input2.node.node_id
    out_validReg(0) := true.B
  }.otherwise {
    out_validReg(0) := false.B
  }
  when(io.input3.valid) {
    mem.write(io.input3.node.node_id, io.input3.node)
    temp_node_idReg(1) := io.input3.node.node_id
    out_validReg(1) := true.B
  }.otherwise {
    out_validReg(1) := false.B
  }
  when(io.input1.valid) {
    mem.write(io.input1.node.node_id, io.input1.node)
    temp_node_idReg(2) := io.input3.node.node_id
    out_validReg(2) := true.B
  }.otherwise {
    out_validReg(2) := false.B
  }
  io.output1.node := mem.read(temp_node_idReg(0))
  io.output2.node := mem.read(temp_node_idReg(1))
  io.output1.valid := out_validReg2(0)
  io.output2.valid := out_validReg2(1)
}
