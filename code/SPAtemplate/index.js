// (C) 2025 notessensei, Apache 2.0

const mainContent = (templateId) => {
  const main = document.getElementsByTagName('main')[0];
  while (main.firstChild) {
    main.removeChild(main.firstChild);
  }
  const template = document.getElementById(`${templateId}Template`);
  if (template) {
    const clone = template.content.cloneNode(true);
    main.appendChild(clone);
    console.log(`Loaded template: ${templateId}`);
  } else {
    console.error(`Template not found: ${templateId}`);
    const errorMessage = document.createElement('p');
    errorMessage.textContent = `Error: Template not found for ${templateId}`;
    main.appendChild(errorMessage);
  }
};

const bootstrap = () => {
  // Initialize the page with any necessary setup
  document.getElementById('loginBtn').addEventListener('click', (event) => {
    event.preventDefault();
    console.log('Login button clicked');
    mainContent('login');
  });
  document.getElementById('listBtn').addEventListener('click', (event) => {
    event.preventDefault();
    console.log('List button clicked');
    mainContent('list');
  });
  document.getElementById('formBtn').addEventListener('click', (event) => {
    event.preventDefault();
    console.log('Form button clicked');
    mainContent('form');
  });
  console.log('Page initialized');
};

/* Load the bootstrap script when the page is ready*/
if (document.readyState != 'loading') {
  bootstrap();
} else {
  document.addEventListener('DOMContentLoaded', bootstrap);
}
