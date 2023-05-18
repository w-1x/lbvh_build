package lbvh

import chisel3._
import hardfloat._
import config.Configs._

class Compute_bigger_bboxIO extends Bundle {
  val input = new Bundle {
    val bbox1 = Input(new BoundingBox)
    val bbox2 = Input(new BoundingBox)
  }

  val out_update_bbox = Output(new BoundingBox)
}

class Compute_bigger_bbox extends Module { // 组合逻辑
  val io = IO(new Compute_bigger_bboxIO)

//找到minx,maxx
  val minx = Module(new Float_CMP_le)
  val maxx = Module(new Float_CMP_le)

  minx.io.a := io.input.bbox1.minPoint.x
  minx.io.b := io.input.bbox2.minPoint.x

  when(minx.io.out_bool) {
    io.out_update_bbox.minPoint.x := io.input.bbox1.minPoint.x
  }.otherwise {
    io.out_update_bbox.minPoint.x := io.input.bbox2.minPoint.x
  }

  maxx.io.a := io.input.bbox1.maxPoint.x
  maxx.io.b := io.input.bbox2.maxPoint.x

  when(maxx.io.out_bool) {
    io.out_update_bbox.maxPoint.x := io.input.bbox2.maxPoint.x
  }.otherwise {
    io.out_update_bbox.maxPoint.x := io.input.bbox1.maxPoint.x
  }
//ymin,ymax
  val miny = Module(new Float_CMP_le)
  val maxy = Module(new Float_CMP_le)

  miny.io.a := io.input.bbox1.minPoint.y
  miny.io.b := io.input.bbox2.minPoint.y

  when(miny.io.out_bool) {
    io.out_update_bbox.minPoint.y := io.input.bbox1.minPoint.y
  }.otherwise {
    io.out_update_bbox.minPoint.y := io.input.bbox2.minPoint.y
  }

  maxy.io.a := io.input.bbox1.maxPoint.y
  maxy.io.b := io.input.bbox2.maxPoint.y

  when(maxy.io.out_bool) {
    io.out_update_bbox.maxPoint.y := io.input.bbox2.maxPoint.y
  }.otherwise {
    io.out_update_bbox.maxPoint.y := io.input.bbox1.maxPoint.y
  }

  // zmin,zmax
  val minz = Module(new Float_CMP_le)
  val maxz = Module(new Float_CMP_le)

  minz.io.a := io.input.bbox1.minPoint.z
  minz.io.b := io.input.bbox2.minPoint.z

  when(minz.io.out_bool) {
    io.out_update_bbox.minPoint.z := io.input.bbox1.minPoint.z
  }.otherwise {
    io.out_update_bbox.minPoint.z := io.input.bbox2.minPoint.z
  }

  maxz.io.a := io.input.bbox1.maxPoint.z
  maxz.io.b := io.input.bbox2.maxPoint.z

  when(maxz.io.out_bool) {
    io.out_update_bbox.maxPoint.z := io.input.bbox2.maxPoint.z
  }.otherwise {
    io.out_update_bbox.maxPoint.z := io.input.bbox1.maxPoint.z
  }
}
