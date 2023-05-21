package lbvh

import config.Configs._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class Sort_primitiveTest extends AnyFlatSpec with ChiselScalatestTester {
  "Sort_primitiveTest" should "pass test" in {
    test(new Sort_primitive_by_mortoncode)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        dut.io.input.valid.poke(1)
        dut.io.input.morton_code.poke(2)
        dut.io.input.id.poke(0)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(3)
        dut.io.input.id.poke(1)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(16)
        dut.io.input.id.poke(2)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(17)
        dut.io.input.id.poke(3)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(18)
        dut.io.input.id.poke(4)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(24)
        dut.io.input.id.poke(5)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(25)
        dut.io.input.id.poke(6)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(0)
        dut.io.input.id.poke(7)
        dut.clock.step(30)
        dut.clock.step(50)
      }
  }
}
