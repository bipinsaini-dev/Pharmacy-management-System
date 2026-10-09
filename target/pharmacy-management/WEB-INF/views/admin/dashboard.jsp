<c:set var="pageTitle" value="Admin Dashboard" />
<c:set var="activeNav" value="dashboard" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row g-3 mb-3">
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/medicines">
    <div class="stat-icon bg-teal"><i class="bi bi-capsule"></i></div>
    <div><div class="stat-label">Total Medicines</div><div class="stat-value">${stats.totalMedicines}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/users">
    <div class="stat-icon bg-blue"><i class="bi bi-person-badge"></i></div>
    <div><div class="stat-label">Total Users</div><div class="stat-value">${stats.totalUsers}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/customers">
    <div class="stat-icon bg-violet"><i class="bi bi-people"></i></div>
    <div><div class="stat-label">Total Customers</div><div class="stat-value">${stats.totalCustomers}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/sales">
    <div class="stat-icon bg-green"><i class="bi bi-currency-rupee"></i></div>
    <div><div class="stat-label">Today's Sales</div><div class="stat-value">&#8377;<fmt:formatNumber value="${stats.todaySales}" minFractionDigits="2" maxFractionDigits="2" /></div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/reports?type=monthly">
    <div class="stat-icon bg-green"><i class="bi bi-graph-up-arrow"></i></div>
    <div><div class="stat-label">Total Sales</div><div class="stat-value">&#8377;<fmt:formatNumber value="${stats.totalSales}" minFractionDigits="2" maxFractionDigits="2" /></div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/medicines?status=LOW">
    <div class="stat-icon bg-amber"><i class="bi bi-exclamation-triangle"></i></div>
    <div><div class="stat-label">Low Stock</div><div class="stat-value">${stats.lowStock}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/medicines?status=EXPIRED">
    <div class="stat-icon bg-red"><i class="bi bi-calendar-x"></i></div>
    <div><div class="stat-label">Expired</div><div class="stat-value">${stats.expired}</div></div></a></div>
  <div class="col-6 col-xl-3"><a class="stat-card" href="${ctx}/admin/medicines?status=NEAR">
    <div class="stat-icon bg-orange"><i class="bi bi-hourglass-split"></i></div>
    <div><div class="stat-label">Near Expiry (90 days)</div><div class="stat-value">${stats.nearExpiry}</div></div></a></div>
</div>

<div class="row g-3">
  <div class="col-lg-6"><div class="card h-100"><div class="card-header">Sales - last 7 days</div>
    <div class="card-body"><div class="chart-box"><canvas id="chart7"></canvas></div></div></div></div>
  <div class="col-lg-6"><div class="card h-100"><div class="card-header">Sales - last 30 days</div>
    <div class="card-body"><div class="chart-box"><canvas id="chart30"></canvas></div></div></div></div>
  <div class="col-lg-6"><div class="card h-100"><div class="card-header">Top selling medicines (units)</div>
    <div class="card-body"><div class="chart-box"><canvas id="chartTop"></canvas></div></div></div></div>
  <div class="col-lg-6"><div class="card h-100"><div class="card-header">Medicines by category</div>
    <div class="card-body"><div class="chart-box"><canvas id="chartCat"></canvas></div></div></div></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
<script>
  (function () {
    var colors = ['#0d9488', '#2563eb', '#7c3aed', '#f59e0b', '#ef4444', '#10b981', '#ec4899', '#64748b'];
    var salesOptions = { maintainAspectRatio: false, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } };
    function line(id, labels, values) {
      new Chart(document.getElementById(id), {
        type: 'line',
        data: { labels: labels, datasets: [{ label: 'Sales', data: values, borderColor: '#0d9488',
                backgroundColor: 'rgba(13,148,136,.15)', fill: true, tension: .35, pointRadius: 3 }] },
        options: salesOptions
      });
    }
    line('chart7', ${stats.last7Labels}, ${stats.last7Values});
    line('chart30', ${stats.last30Labels}, ${stats.last30Values});
    new Chart(document.getElementById('chartTop'), {
      type: 'bar',
      data: { labels: ${stats.topLabels}, datasets: [{ label: 'Units sold', data: ${stats.topValues}, backgroundColor: colors }] },
      options: { maintainAspectRatio: false, indexAxis: 'y', plugins: { legend: { display: false } } }
    });
    new Chart(document.getElementById('chartCat'), {
      type: 'doughnut',
      data: { labels: ${stats.catLabels}, datasets: [{ data: ${stats.catValues}, backgroundColor: colors }] },
      options: { maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
    });
  })();
</script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
