<%-- Flash messages (toast for success, alert for errors) and validation error lists. --%>
<c:if test="${not empty flashSuccess}">
  <div class="toast-container position-fixed top-0 end-0 p-3 no-print">
    <div class="toast text-bg-success border-0 js-auto-toast" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body"><i class="bi bi-check-circle me-2"></i><c:out value="${flashSuccess}" /></div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  </div>
</c:if>
<c:if test="${not empty flashError}">
  <div class="alert alert-danger alert-dismissible fade show no-print" role="alert">
    <i class="bi bi-exclamation-triangle me-2"></i><c:out value="${flashError}" />
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
</c:if>
<c:if test="${not empty error}">
  <div class="alert alert-danger alert-dismissible fade show no-print" role="alert">
    <i class="bi bi-exclamation-triangle me-2"></i><c:out value="${error}" />
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  </div>
</c:if>
<c:if test="${not empty errors}">
  <div class="alert alert-danger no-print" role="alert">
    <strong>Please fix the following:</strong>
    <ul class="mb-0">
      <c:forEach var="e" items="${errors}"><li><c:out value="${e}" /></li></c:forEach>
    </ul>
  </div>
</c:if>
