import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import SimpleConfigSource from "@site/src/components/examples/SimpleConfigSource";
import SimpleConfigYaml from "@site/src/components/examples/SimpleConfigYaml";

import styles from './index.module.css';

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx('hero hero--primary', styles.heroBanner)}>
      <div className="container">
        <h1 className="hero__title">{siteConfig.title}</h1>
        <p className="hero__subtitle">{siteConfig.tagline}</p>
        <div className={styles.buttons}>
          <Link
            className="button button--secondary button--lg"
            to="/docs/intro">
            Get started
          </Link>
        </div>
      </div>
    </header>
  );
}

export default function Home(): JSX.Element {
  return (
    <Layout>
      <HomepageHeader />
      <main>
        <div className="container">
          <div className="text--center margin-vert--lg">
            <h3>You can read and write config files just by defining a class constructor.</h3>
          </div>
          <div className="row">
            <div className="col">
              <SimpleConfigSource />
            </div>
            <div className="col">
              <SimpleConfigYaml />
            </div>
          </div>
        </div>
      </main>
    </Layout>
  );
}
