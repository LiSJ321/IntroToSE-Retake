<template>
  <div>
    <el-card style="width: 50%">
      <el-form :model="user" label-width="100px" style="padding-right: 50px">
        <div style="margin: 15px; text-align: center">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="user.avatar" :src="user.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="username" prop="username">
          <el-input v-model="user.username" placeholder="username" disabled></el-input>
        </el-form-item>
        <el-form-item label="your name" prop="name">
          <el-input v-model="user.name" placeholder="your name"></el-input>
        </el-form-item>
        <el-form-item label="Telephone" prop="phone">
          <el-input v-model="user.phone" placeholder="Telephone"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="user.email" placeholder="Email"></el-input>
        </el-form-item>
        <el-form-item label="Balance" prop="balance">
          <el-input v-model="user.account" disabled placeholder="Balance"></el-input>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">SAVE</el-button>
          <el-button type="warning" @click="recharge">Top Up</el-button>
        </div>
      </el-form>
    </el-card>
    <el-dialog title="Top Up" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item prop="account" label="Amount">
          <el-input v-model="account" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="pay">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "UserPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      account: null,
      fromVisible: false,
      rules: {
        account: [
          {required: true, message: 'Please input amount', trigger: 'blur'},
        ]
      },
    }
  },
  created() {
    this.getPerson()
  },
  methods: {
    getPerson() {
      this.$request.get('/user/selectById/' + this.user.id).then(res => {
        if (res.code === '200') {
          this.user = res.data
          localStorage.setItem('xm-user', JSON.stringify(this.user))
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    recharge(){
      this.account = 100
      this.fromVisible = true
    },
    pay(){
      this.user.account = parseFloat(this.account) + parseFloat(this.user.account)
      this.update()
      this.fromVisible = false
    },
    update() {
      // 保存当前的用户信息到数据库
      this.$request.put('/user/update', this.user).then(res => {
        if (res.code === '200') {
          // 成功更新
          this.$message.success('Save successfully')

          // 更新浏览器缓存里的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.user))

          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把user的头像属性换成上传的图片的链接
      this.$set(this.user, 'avatar', response.data)
    },
  }
}
</script>

<style scoped>
/deep/.el-form-item__label {
  font-weight: bold;
}
/deep/.el-upload {
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border-radius: 50%;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>