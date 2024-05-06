<template>
  <div>
    <div class="search">
      <el-input placeholder="Input Product Name" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">Inquiry</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">Reset</el-button>
    </div>

    <div class="table">
      <div style="padding: 10px 15px">
        <el-row>
          <el-col :span="6" v-for="item in tableData" style="margin-bottom: 20px">
            <div style="display: flex">
              <div style="flex: 1; padding-right: 5px">
                <img :src="item.img" alt="" style="width: 100%; height: 155px; border-radius: 10px; border: 1px solid #cccccc">
              </div>
              <div style="flex: 1; padding-left: 5px">
                <div style="font-size: 15px; font-weight: 550; color: #817a70">{{ item.name }}</div>
                <div style="margin-top: 5px; font-weight: 600; font-size: 20px; color: red">￥ {{ item.price }}</div>
                <div style="margin-top: 5px; font-size: 13px; color: #4b4949">Remaining quantity：{{ item.num }}</div>
                <div style="margin-top: 10px">
                  <el-input-number v-model="item.tmpNum" :min="1" :max="item.num" size="mini"></el-input-number>
                </div>
                <div style="margin-top: 15px">
                  <el-button type="warning" size="mini" :disabled="item.num === 0"@click="handleEdit(item)">Purchase</el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
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
    <el-dialog title="Delivery Information" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username" label="Receiver">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="phone" label="Telephone">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="address" label="Address">
          <el-input v-model="form.address" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="buy">Confirm</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
export default {
  name: "GoodsUser",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 12,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          {required: true, message: 'Please input Receiver', trigger: 'blur'},
        ],
        phone: [
          {required: true, message: 'Please input Phone number', trigger: 'blur'},
        ],
        address: [
          {required: true, message: 'Please input Address', trigger: 'blur'},
        ],
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    buy() {
      let data = {
        userId: this.user.id,
        goodsId: this.form.id,
        username: this.form.username,
        phone: this.form.phone,
        address: this.form.address,
        num: this.form.tmpNum,
        status: 'Shipping',
        price: parseFloat(this.form.tmpNum) * parseFloat(this.form.price)
      }
      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Purchase successful')
          this.fromVisible = false
          this.load(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/goods/update' : '/goods/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('Save success')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('Are you sure？', 'confirm', {type: "warning"}).then(response => {
        this.$request.delete('/goods/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('Please choose data')
        return
      }
      this.$confirm('Are you sure？', 'Confirm', {type: "warning"}).then(response => {
        this.$request.delete('/goods/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('Success')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/goods/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      this.form.img = response.data
    },
  }
}
</script>

<style scoped>

</style>
