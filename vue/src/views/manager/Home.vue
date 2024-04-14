<template>
  <div>
    <div class="card" style="padding: 15px">
      HI,{{ user?.name }}！Welcome.
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 100%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">Announcement list</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
        <!--   宠物卡片区     -->
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold; color: #817a70">If you like me, adopt me~</div>
        <div>
          <el-row>
            <el-col :span="6" v-for="item in animalData">
              <div class="card" style="background-color: #f8f4e8; text-align: center">
                <img :src="item.img" alt="" style="width: 70px; height: 80px; border-radius: 50%">
                <div style="font-weight: bold; color: #817a70; margin-top: 5px">{{ item.name }}（{{ item.type }}）</div>
                <div style="color: #575353; font-size: 13px; margin-top: 5px">Sex：{{ item.sex }}， Age：{{ item.age }}， <span style="color: #7d3d0c">{{ item.status }}</span></div>
                <div style="margin-top: 15px; color: #4b4949; text-align: left">Get to know me：{{ item.descr }}</div>
                <div>
                  <el-button type="success" size="mini" style="margin-top: 20px":disabled="item.status === 'Adopted'" @click="adopt(item)">Adopt</el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      animalData: [],
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    this.loadAnimal()
  },
  methods: {
    loadAnimal() {
      this.$request.get('/animal/selectAll').then(res => {
        if (res.code === '200') {
          this.animalData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    adopt(animal) {
      if (this.user.role === 'ADMIN') {
        this.$message.warning('Your role does not support this operation')
        return
      }
      let data = JSON.parse(JSON.stringify(animal))
      data.status = 'Adopted'
    this.$request.put('/animal/update',data).then(res=>{
      if(res.code==='200'){
        this.$message.success('The adoption is successful')
        this.loadAnimal()
        //向领养记录同步一条信息
        let adoptData ={
          userId: this.user.id,
          animalId: data.id
        }
        this.$request.post('/adopt/add',adoptData).then(res =>{
          if(res.code === '200'){
            this.$message.success('Please treat it well')
          }else{
            this.$message.error(res.msg)
          }
        })
      }
    })
    }
  }
}
</script>
<style scoped>
.el-col-5{
  width: 20%;
  max-width: 20%;
  padding: 10px 10px;
}
</style>
