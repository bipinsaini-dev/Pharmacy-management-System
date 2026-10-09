<%-- Page frame (bottom): closes the layout, shared delete-confirmation modal and scripts. --%>
    </main>
    <footer class="app-footer no-print">PharmaCare Pharmacy Management System &middot; Java Servlets, JSP, JDBC &amp; MySQL</footer>
  </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="post" id="deleteForm">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="id" id="deleteId">
      <div class="modal-header">
        <h5 class="modal-title"><i class="bi bi-trash text-danger me-2"></i>Confirm delete</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">Are you sure you want to delete <strong id="deleteName">this record</strong>? This cannot be undone.</div>
      <div class="modal-footer">
        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
        <button type="submit" class="btn btn-danger">Delete</button>
      </div>
    </form>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/app.js"></script>
</body>
</html>
