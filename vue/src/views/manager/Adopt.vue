<template>
  <div>
    <div class="search">
      <el-select v-model="animalId" placeholder="please choose pet" style="width: 200px" v-if="user.role === 'ADMIN'">
        <el-option v-for="item in animalData" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-select v-model="status" placeholder="Please choose status" style="width: 200px; margin-left: 5px">
        <el-option label="under adoption" value="under adoption"></el-option>
        <el-option label="has been returned" value="has been returned"></el-option>
      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">Inquiry</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">Reset</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="animalImg" label="animal avatar" show-overflow-tooltip>
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 50%" v-if="scope.row.animalImg"
                        :src="scope.row.animalImg" :preview-src-list="[scope.row.animalImg]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="animalName" label="animal name" show-overflow-tooltip></el-table-column>
        <el-table-column prop="userName" label="adopter name" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="adoption time"></el-table-column>
        <el-table-column prop="status" label="status"></el-table-column>

        <el-table-column label="operate" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="danger" size="mini" :disabled="scope.row.status === 'has been returned'" @click=cancel(scope.row)>give up for adoption</el-button>
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
  name: "Adopt",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      status: null,
      animalId: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: [],
      animalData:[]
    }
  },
  created() {
    this.load(1)
    this.loadAnimal()
  },
  methods: {
    loadAnimal() {
      this.$request.get('/adopt/selectChange').then(res => {
        if (res.code === '200') {
          this.animalData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$request({
        url: this.form.id ? '/adopt/update' : '/adopt/add',
        method: this.form.id ? 'PUT' : 'POST',
        data: this.form
      }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('Save successfully')
          this.load(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    cancel(row) {
      this.$confirm('You already have a relationship, are you sure you want to give it up?', 'Last Ask', {type: "warning"}).then(response => {
        this.form = JSON.parse(JSON.stringify(row))
        this.form.status = 'has been returned'
        this.save();
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('please choose data')
        return
      }
      this.$confirm('are you sure？', 'confirm', {type: "warning"}).then(response => {
        this.$request.delete('/adopt/delete/batch', {data: this.ids}).then(res => {
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
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/adopt/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: this.status,
          animalId: this.animalId,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.status = null
      this.animalId = null
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
