<template>
  <div>
    <div class="search">
      <el-input placeholder="Please input order id" style="width: 200px" v-model="orderId"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">Inquiry</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">Reset</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe >
        <el-table-column prop="id" label="ID" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="img" label="Picture" show-overflow-tooltip>
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px" v-if="scope.row.goodsImg"
                        :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="Name" show-overflow-tooltip></el-table-column>
        <el-table-column prop="buyName" label="Orderer" show-overflow-tooltip></el-table-column>
        <el-table-column prop="orderId" label="Order Id"></el-table-column>
        <el-table-column prop="time" label="Order time"></el-table-column>
        <el-table-column prop="username" label="Receiver"></el-table-column>
        <el-table-column prop="phone" label="Telephone"></el-table-column>
        <el-table-column prop="address" label="Address"></el-table-column>
        <el-table-column prop="num" label="Quantity"></el-table-column>
        <el-table-column prop="price" label="Price"></el-table-column>
        <el-table-column prop="status" label="Status"></el-table-column>

        <el-table-column label="operate" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="changeStatus(scope.row, 'Received')" size="mini" v-if="scope.row.status === 'Delivering' && user.role === 'USER'">Confirm receipt</el-button>
            <el-button plain type="primary" @click="changeStatus(scope.row, 'Delivering')" size="mini" v-if="scope.row.status === 'Shipping' && user.role === 'ADMIN'">Delivery</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Orders",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      orderId: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    changeStatus(row, status) {
      let data = JSON.parse(JSON.stringify(row))
      data.status = status
      this.$request.put('/orders/update', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Success')
          this.load(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderId: this.orderId,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.orderId = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
