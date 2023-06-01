package lbvh

import chisel3._
import chisel3.util._
import hardfloat._
import config.Configs._

class Sort_primitive_by_mortoncodeIO extends Bundle {
  val input = new Bundle {
    val morton_code = Input(UInt(Morton_WIDTH.W))
    val id = Input(UInt(ADDR_WIDTH.W))
    val valid = Input(Bool())
  }

  val output = new Bundle {
    val morton_code = Output(UInt(Morton_WIDTH.W))
    val indice = Output(UInt(ADDR_WIDTH.W))
    val valid = Output(Bool())
  }
}

class Sort_primitive_by_mortoncode extends Module {

  // 目前只能计算6位morton码，也就是跑一下8个图元的例子验证一下逻辑
  val io = IO(new Sort_primitive_by_mortoncodeIO)

  val clock_count_reg = RegInit(0.U(DATA_WIDTH.W)) // 时钟计数
  val clockaddstart = RegInit(false.B)

  val idReg = Reg(Vec(DEPTH, UInt(ADDR_WIDTH.W)))
  val mask = WireInit(MASK.U(ADDR_WIDTH.W))

  val tempMortonReg = Reg(Vec(DEPTH, UInt(Morton_WIDTH.W))) // 保存所有读入的数据
  val bucketReg = Reg(
    Vec(Bucket_count, UInt(DATA_WIDTH.W))
  ) // 记录桶中有多少morton码与该桶序列相同的图元
  val add_bucketReg = Reg(
    Vec(Bucket_count, UInt(DATA_WIDTH.W))
  ) // 求出处于该位置的图元的排序
  val out_orderReg = Reg(
    Vec(DEPTH, UInt(DATA_WIDTH.W))
  ) // 输出morton码对应的图元的输入顺序

  val outMortonREG = Reg(Vec(DEPTH, UInt(Morton_WIDTH.W))) // 交换morton顺序
  val outindiceREG = Reg(Vec(DEPTH, UInt(ADDR_WIDTH.W)))

  val out_valid = WireInit(false.B)

  when(io.input.valid) { // 初始化排序数据 需要DEPTH个周期,这里考虑边读数据边对bucketReg进行计算
    tempMortonReg(clock_count_reg) := io.input.morton_code
    when(clock_count_reg < DEPTH.U) {
      bucketReg(io.input.morton_code) := bucketReg(io.input.morton_code) + 1.U
      idReg(clock_count_reg) := io.input.id
    }

  }
  when(clock_count_reg >= DEPTH.U) { // 需要 Bucket_count - 1 个周期
    add_bucketReg(0) := 0.U // 求出数据的位置
    for (i <- 1 until (Bucket_count)) {
      add_bucketReg(i) := add_bucketReg(i - 1) + bucketReg(i - 1)
    }
  }

  for (i <- 0 until (DEPTH)) { // 求出最后的排序，需要DEPTH个周期
    when(clock_count_reg === DEPTH.U + Bucket_count.U + i.U - 1.U) {
      out_orderReg(i) := add_bucketReg(tempMortonReg(i))
      add_bucketReg(tempMortonReg(i)) := add_bucketReg(tempMortonReg(i)) + 1.U
    }
  }
//out_orderReg(i) 在 DEPTH.U + Bucket_count.U + i.U 有效
//outMortonReg在DEPTH.U + Bucket_count.U + i.U + 1.U有效
  for (i <- 0 until (DEPTH)) { // 缓存在输出buffer中,需要DEPTH个周期
    when(clock_count_reg === DEPTH.U + Bucket_count.U + i.U) {
      outMortonREG(out_orderReg(i)) := tempMortonReg(i)
      outindiceREG(out_orderReg(i)) := idReg(i)
    }
  }

  when(io.input.valid) {
    clockaddstart := true.B
  }
  when(clockaddstart || io.input.valid) {
    clock_count_reg := clock_count_reg + 1.U
  }
  when(
    clock_count_reg >= 2.U * DEPTH.U + Bucket_count.U &&
      clock_count_reg < 2.U * DEPTH.U + Bucket_count.U + DEPTH.U
  ) {
    out_valid := true.B
  }.otherwise {
    out_valid := false.B
  }

  io.output.indice := outindiceREG(
    (clock_count_reg - (2.U * DEPTH.U + Bucket_count.U)) % DEPTH.U
  )

  io.output.morton_code := outMortonREG(
    (clock_count_reg - (2.U * DEPTH.U + Bucket_count.U)) % DEPTH.U
  )
  io.output.valid := out_valid

}
