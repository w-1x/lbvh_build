package lbvh

import config.Configs._
import chisel3.util._
import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class Primitive_StoreTest extends AnyFlatSpec with ChiselScalatestTester {
  "Primitive_StoreTest" should "pass test" in {
    test(new Create_leaves)
      .withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
        for (i <- 0 until (80)) {
          dut.io.input.valid.poke(0)
          dut.io.input.morton_code.poke(0)
          dut.io.input.indice.poke(0)
          dut.clock.step(1)
        }

        dut.io.input.valid.poke(1)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(0)
        dut.io.input.indice.poke(6)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(2)
        dut.io.input.indice.poke(0)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(2)
        dut.io.input.indice.poke(1)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(3)
        dut.io.input.indice.poke(7)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(17)
        dut.io.input.indice.poke(2)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(18)
        dut.io.input.indice.poke(3)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(24)
        dut.io.input.indice.poke(4)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(25)
        dut.io.input.indice.poke(5)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(0)
        dut.io.input.indice.poke(6)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(2)
        dut.io.input.indice.poke(0)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(2)
        dut.io.input.indice.poke(1)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(3)
        dut.io.input.indice.poke(7)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(17)
        dut.io.input.indice.poke(2)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(18)
        dut.io.input.indice.poke(3)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(24)
        dut.io.input.indice.poke(4)
        dut.clock.step(1)
        dut.io.input.morton_code.poke(25)
        dut.io.input.indice.poke(5)
        dut.clock.step(10)
      }
  }
}
