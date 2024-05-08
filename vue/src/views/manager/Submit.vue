<template>
  <div>
    <div class="search">
      <el-input placeholder="Please enter a keyword" style="width: 200px" v-model="name"></el-input>
      <el-select v-model="status" placeholder="Please choose status" style="width: 200px; margin-left: 5px">
        <el-option label="Waiting for help" value="Waiting for help"></el-option>
        <el-option label="Rescued" value="Rescued"></el-option>
      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">Inquiry</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">Reset</el-button>
    </div>

    <div class="operation" v-if="user.role === 'USER'">
      <el-button type="primary" plain @click="handleAdd">Report</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="Id" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="img" label="Picture" show-overflow-tooltip>
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px" v-if="scope.row.img"
                        :src="scope.row.img" :preview-src-list="[scope.row.img]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Reporting Instructions" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="Time"></el-table-column>
        <el-table-column prop="status" label="Status"></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'ADMIN'">
          <template v-slot="scope">
            <el-button plain type="primary" :disabled="scope.row.status === 'Rescued'" @click="deal(scope.row, 'Rescued')" size="mini">Rescued</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>Delete</el-button>
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

    <el-dialog title="Please report true and valid information!" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="img" label="Upload img">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">Upload image</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="name" label="Reportings Instructions">
          <el-input type="textarea" :rows="4" v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
export default {
  name: "Submit",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      status: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          {required: true, message: 'Please enter the report description', trigger: 'blur'},
        ],
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleAdd() {   // 新增数据
      this.form = {
        status: 'Waiting for help'
      }  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    deal(row, status) {
      let data = JSON.parse(JSON.stringify(row))
      data.status = status
      this.$request.put('/submit/update', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Success')
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
            url: this.form.id ? '/submit/update' : '/submit/add',
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
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('Are you sure to delete this?', 'Last confirmation', {type: "warning"}).then(response => {
        this.$request.delete('/submit/delete/' + id).then(res => {
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
      this.$request.get('/submit/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          status: this.status,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.status = null
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
