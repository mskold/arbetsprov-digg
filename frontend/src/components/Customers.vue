<template>
  <div>
    <h1>Kunder</h1>
    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>Namn</th>
        <th>Adress</th>
        <th>E-post</th>
        <th>Telefon</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="customer in paginatedCustomers" :key="customer.id">
        <td>{{ customer.id }}</td>
        <td>{{ customer.name }}</td>
        <td>{{ customer.address }}</td>
        <td>{{ customer.email }}</td>
        <td>{{ customer.telephone }}</td>
      </tr>
      </tbody>
    </table>
    <hr/>
    <div class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1">Föregående</button>
      <span>Sida {{ currentPage }} av {{ numberOfPages }}</span>
      <button @click="nextPage" :disabled="currentPage === numberOfPages">Nästa</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const users = ref([])
const currentPage = ref(1)
const pageSize = 5

/*
  För en tjänst med många poster så hade jag implementerat paginering mot backend,
  men när det handlar om så här små datamängder valde jag att lägga hela pagineringslogiken här i frontend.
 */
onMounted(async () => {
  const res = await fetch('/api/customers')
  users.value = await res.json()
})

const numberOfPages = computed(() =>
    Math.ceil(users.value.length / pageSize)
)

const paginatedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return users.value.slice(start, start + pageSize)
})

function nextPage() {
  if (currentPage.value < numberOfPages.value) {
    currentPage.value++
  }
}

function prevPage() {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}
</script>
