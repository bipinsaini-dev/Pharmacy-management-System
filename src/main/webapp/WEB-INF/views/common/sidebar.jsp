<aside class="sidebar" id="sidebar">
  <a class="brand" href="${ctx}/${base}/dashboard"><i class="bi bi-capsule-pill"></i><span>PharmaCare</span></a>
  <nav class="nav flex-column">
    <a class="nav-link ${activeNav eq 'dashboard' ? 'active' : ''}" href="${ctx}/${base}/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a>
    <a class="nav-link ${activeNav eq 'billing' ? 'active' : ''}" href="${ctx}/${base}/billing"><i class="bi bi-receipt"></i> New Bill</a>
    <a class="nav-link ${activeNav eq 'medicines' ? 'active' : ''}" href="${ctx}/${base}/medicines"><i class="bi bi-capsule"></i> ${base eq 'admin' ? 'Medicines' : 'Inventory'}</a>
    <c:if test="${base eq 'pharmacist'}">
      <a class="nav-link" href="${ctx}/pharmacist/medicines?action=add"><i class="bi bi-plus-circle"></i> Add medicine</a>
    </c:if>
    <a class="nav-link ${activeNav eq 'customers' ? 'active' : ''}" href="${ctx}/${base}/customers"><i class="bi bi-people"></i> Customers</a>
    <a class="nav-link ${activeNav eq 'sales' ? 'active' : ''}" href="${ctx}/${base}/sales"><i class="bi bi-cash-coin"></i> ${base eq 'admin' ? 'Sales' : 'My Bills'}</a>
    <c:if test="${base eq 'admin'}">
      <div class="nav-section">Management</div>
      <a class="nav-link ${activeNav eq 'purchases' ? 'active' : ''}" href="${ctx}/admin/purchases"><i class="bi bi-truck"></i> Purchases</a>
      <a class="nav-link ${activeNav eq 'suppliers' ? 'active' : ''}" href="${ctx}/admin/suppliers"><i class="bi bi-building"></i> Suppliers</a>
      <a class="nav-link ${activeNav eq 'categories' ? 'active' : ''}" href="${ctx}/admin/categories"><i class="bi bi-tags"></i> Categories</a>
      <a class="nav-link ${activeNav eq 'users' ? 'active' : ''}" href="${ctx}/admin/users"><i class="bi bi-person-gear"></i> Users</a>
      <a class="nav-link ${activeNav eq 'reports' ? 'active' : ''}" href="${ctx}/admin/reports"><i class="bi bi-bar-chart-line"></i> Reports</a>
    </c:if>
    <div class="nav-section">Account</div>
    <a class="nav-link ${activeNav eq 'profile' ? 'active' : ''}" href="${ctx}/${base}/profile"><i class="bi bi-person-circle"></i> My Profile</a>
    <a class="nav-link" href="${ctx}/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
  </nav>
</aside>
