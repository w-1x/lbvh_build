package hardfloat

import Chisel._
// import chisel3._
import chisel3.util._

class Float_DIV() extends Module {
  val io = IO(new Bundle {
    // val inValid         = Input(Bool()) //输入是否有效
    val a = Input(Bits(32.W))
    val b = Input(Bits(32.W))
    val valid = Input(Bool())
    val out = Output(Bits(32.W))
    val ready = Output(Bool())
  })

  val fdiv = Module(new DivSqrtRecFN_small(8, 24, 0))
  // stage(1)
  fdiv.io.inValid := io.valid
  fdiv.io.a := io.a
  fdiv.io.b := io.b
  fdiv.io.roundingMode := 1.U
  fdiv.io.detectTininess := 1.U
  io.out := fNFromRecFN(8, 24, fdiv.io.out)
  io.ready := fdiv.io.inReady
}

// class fdiv_Simple (dut:FDIV) extends PeekPokeTester(dut){
//   poke(dut.io.valid,1.U)
//   poke(dut.io.a,1067450368.U)
//   poke(dut.io.b,1073741824.U)
//   step(1)
//   poke(dut.io.valid,0.U)
//   // poke(dut.io.hiT_in,3.S)
//   // poke(dut.io.Oz_in,4.U)
//   // poke(dut.io.invDz_div,1056964608.U)
//   // poke(dut.io.v11_in,5.U)
//   // poke(dut.io.v22_in,6.U)
//   // poke(dut.io.Div_en,1.U)
//   step(24)
//   poke(dut.io.valid,1.U)

//   poke(dut.io.a,1067450322.U)
//   poke(dut.io.b,1073741822.U)

//   step(1)
//   poke(dut.io.valid,0.U)
//   step(50)

//   }
// object FDIV_dstest extends App {
//   chisel3.iotesters.Driver.execute(args, () => new FDIV())(c => new fdiv_Simple(c))
// }
