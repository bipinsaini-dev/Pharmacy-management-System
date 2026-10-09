<header class="topbar no-print">
  <button class="btn btn-light d-lg-none" id="sidebarToggle" type="button" aria-label="Toggle menu"><i class="bi bi-list fs-5"></i></button>
  <h1 class="page-title"><c:out value="${pageTitle}" /></h1>
  <div class="ms-auto dropdown">
    <a href="#" class="user-chip dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
      <span class="avatar"><c:out value="${fn:toUpperCase(fn:substring(sessionScope.user.fullName, 0, 1))}" /></span>
      <span class="d-none d-sm-inline">
        <strong><c:out value="${sessionScope.user.fullName}" /></strong>
        <small class="text-muted d-block"><c:out value="${sessionScope.user.role}" /></small>
      </span>
    </a>
    <ul class="dropdown-menu dropdown-menu-end shadow-sm">
      <li><a class="dropdown-item" href="${ctx}/${base}/profile"><i class="bi bi-key me-2"></i>Change password</a></li>
      <li><hr class="dropdown-divider"></li>
      <li><a class="dropdown-item text-danger" href="${ctx}/logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
    </ul>
  </div>
</header>
